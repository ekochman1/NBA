var stompClient = null;
var username = null;
var leagueID = null;

var chatPage = document.querySelector('#chat-page1');
var consolePage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm1');
var consoleForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea1');
var consoleArea = document.querySelector('#messageArea');
var connectingElement = document.querySelectorAll('.connecting');

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect() {
    username = sessionStorage.getItem('username');
    leagueID = sessionStorage.getItem('leagueID').toString();
    if(username) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnectedLeague, onError);
    }
}

function onConnectedLeague() {
    // Subscribe to the league chat
    stompClient.subscribe('/draft.'+leagueID, onMessageReceived);


    // Tell your username to the server
    stompClient.send('/app/chat.addUser.'+leagueID,
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement[0].classList.add('hidden');
    connectingElement[1].classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendDraft(playerRankOverall, name) {
    var messageContent = playerRankOverall;
    if(messageContent && stompClient) {
        var draftPick = {
            sender: username,
            pick: playerRankOverall,
            player: name,
            type: 'DRAFT'
        };
        stompClient.send('/app/draft.sendPick.'+leagueID, {}, JSON.stringify(draftPick));
    }
}

function startDraft() {
    if (stompClient) {
        var draftMessage = {
            sender: username;
            pick: -1,
            player: "",
            type: 'START'
        };
        stompClient.send('/app/draft.sendPick.'+leagueID, {}, JSON.stringify(draftMessage));
    }
}

function endDraft(){
    if (stompClient) {
        var draftMessage = {
            sender: username,
            pick: -1,
            player: "",
            type: 'DONE'
        };
        stompClient.send('/app/draft.sendPick.'+leagueID, {}, JSON.stringify(draftMessage));
    }
}

function sendDraftMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send('/app/draft.sendMessage.'+leagueID, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        consoleArea.appendChild(messageElement);
        consoleArea.scrollTop = messageArea.scrollHeight;
    } else if (message.type === 'START') {
        draftQuery()
        alert("The draft is starting!");
    }else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);
        consoleArea.appendChild(messageElement);
        consoleArea.scrollTop = messageArea.scrollHeight;
    } else if (message.type === 'DRAFT'){
        document.getElementById(message.pick).remove();
        messageElement.classList.add('event-message');
        message.content = message.sender + ' drafted ' + message.player;
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        consoleArea.appendChild(messageElement);
        consoleArea.scrollTop = messageArea.scrollHeight;
    } else if (message.type === 'DONE') {
        alert("Draft is completed");
        window.location = 'http://ec2-54-215-176-11.us-west-1.compute.amazonaws.com/homepage2.html';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);

        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
    }
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

messageForm.addEventListener('submit', sendDraftMessage, true)

function draftQuery(){
	var obj = {};
	obj.leagueID = sessionStorage.getItem("leagueID");
	$.ajax({
		type: 'GET',						//get data from server to the client
		url: 'http://ec2-54-215-176-11.us-west-1.compute.amazonaws.com/createDraft?leagueID='+obj.leagueID,	//want to get info from here, it's in a JSONObject
	})
	.done(function(data) {
		document.getElementById("draftTable").style.display="block";
		var tbody = $('#draftTable tbody'), props = ["name", "position", "teamCode", "playerRankOverall", "playerRankPos", "salary", "playerID"];
	    $.each(data, function(i, data) {
		    var tr = $('<tr id="'+data["playerRankOverall"]+'"><td class="nr">' + '<button type="button" id="'+data["playerID"]+'" class="use-address" onclick="Click(this.id)">Draft</button>' + '</td></tr>');
		        $.each(props, function(i, prop) {
		            $('<td>').html(data[prop]).appendTo(tr);
		        });
		    tbody.append(tr);
	    });
	})
	.fail(function(data) {
	    alert("Failed to load data.");
    });
}