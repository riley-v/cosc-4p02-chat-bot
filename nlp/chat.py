import sys

from chatbot import ChatBot

bot = ChatBot()

sentence = str(sys.argv[1])
#sentence = "Who teaches Cosc 4p02?"
print(f"{bot.getResponse(sentence)}")