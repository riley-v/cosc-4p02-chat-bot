import sys

from chatbot import ChatBot

bot = ChatBot()

sentence = str(sys.argv[1])
#sentence = "I didn't take COSC 4P91."
print(f"{bot.getResponse(sentence)}")