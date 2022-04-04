const express = require('express');
const socketio = require('socket.io');
const http = require('http');


const app = express();
const server = http.createServer(app);
const io = socketio(server);

//Connects to front end, receives and sends messages

//set static folder
app.use(express.static('public'));


//sending Initial message to html
io.on('connection', socket => {
	//console.log('New WS Connection...');
	socket.emit('message', `Welcome to Brock Chat Bot`);

//Listen for chat message
socket.on('chatMessage', (msg) => {
console.log(msg);

//Connects to NLP python script
const { spawn } = require('child_process');
const childPython = spawn('python',['newchat.py',msg]);

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


server.listen(3000, () => console.log('listening at 3000')); //sets up local server


