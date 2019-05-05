var stompClient = null;
var username = null;
var leagueID = null;

var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

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
            type: 'DRAFT',
            lid: leagueID
        };
        stompClient.send('/app/draft.sendPick.'+leagueID, {}, JSON.stringify(draftPick));
    }
}

function sendDraftMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var draftMessage = {
            sender: username,
            pick: messageInput.value,
            type: 'CHAT',
            lid: leagueID
        };
        stompClient.send('/app/draft.sendMessage.'+leagueID, {}, JSON.stringify(draftMessage));
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
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else if (message.type === 'DRAFT'){
        document.getElementById(message.pick).remove();
        messageElement.classList.add('event-message');
        message.content = message.sender + ' drafted ' + message.name;
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
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}