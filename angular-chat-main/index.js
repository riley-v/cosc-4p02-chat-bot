const express = require('express')
const cors = require('cors')
const Pusher = require("pusher");

const pusher = new Pusher({
  appId: "1372563",
  key: "314f9f4f7e2ab59df684",
  secret: "9479ef9822cecb42092c",
  cluster: "us2",
  useTLS: true
});

const app = express();

app.use(cors({
origin: ['http://localhost:4200']
}))

app.use(express.json())

app.post('/api/messages', async (req, res) => {
await pusher.trigger("chatbot", "message", {
  username: req.body.username,
  message: req.body.message
});

res.json([req.body.message]);

//res.json([]);
})

console.log("listening to port 8000")
app.listen(8000)

