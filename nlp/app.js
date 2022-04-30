const express = require('express');
const socketio = require('socket.io');
const http = require('http');
const path = require('path');

const app = express();
const server = http.createServer(app);
const io = socketio(server);
const port = process.env.PORT || 3000

//Connects to front end, receives and sends messages

//sending Initial message to html
io.on('connection', socket => {
	//console.log('New WS Connection...');
	socket.emit('message', `Welcome to the Brock chatbot`);

//Listen for chat message
socket.on('chatMessage', (msg) => {
console.log(msg);

//Connects to NLP python script
const { spawn } = require('child_process');
const childPython = spawn('python',['chat.py',msg]);

//prints back response
childPython.stdout.on('data', (data) => {
console.log(`${data}`);

//sends message back to client
io.emit('message',`${data}`);
});

//prints error message
childPython.stderr.on('data', (data) => {
	console.error(`stderr: ${data}`);
});

//closes program
childPython.on('close', (code) => {
	console.log(`child process exited  with code ${code}`);
});

});

});

//set static folder
app.use(express.static(path.join(__dirname, 'public')));

//server.listen(3000, 'localhost', () => console.log('listening at 3000')); //sets up local server
server.listen(port, () => console.log('listening at ' + port)); //sets up local server


