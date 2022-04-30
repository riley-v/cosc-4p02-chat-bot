import sys
import io
 
from chatbot import ChatBot

bot = ChatBot()
sys.stdout.reconfigure(encoding='latin-1')

sentence = str(sys.argv[1])
#sentence = "Who teaches Cosc 4p02?"
print(f"{bot.getResponse(sentence)}")