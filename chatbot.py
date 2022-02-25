import random
import json
import torch
import spacy
import contextualSpellCheck

from model import NeuralNet
from nltk_utils import bag_of_words, tokenize

class ChatBot:

    def __init__(self):
        self.device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

        with open('intents.json', 'r') as json_data:
            self.intents = json.load(json_data)

        FILE = "data.pth"
        data = torch.load(FILE)

        input_size = data["input_size"]
        hidden_size = data["hidden_size"]
        output_size = data["output_size"]
        self.all_words = data['all_words']
        self.tags = data['tags']
        model_state = data["model_state"]

        self.model = NeuralNet(input_size, hidden_size, output_size).to(self.device)
        self.model.load_state_dict(model_state)
        self.model.eval()

        self.nlp = spacy.load('en_core_web_sm')
        contextualSpellCheck.add_to_pipe(self.nlp)
        
        # read in ignore words for nlp spell check
        with open("IgnoreWords.txt") as f:
            self.ignore_words = f.read().splitlines()
            for i, word in enumerate(self.ignore_words):
                self.ignore_words[i] = word.casefold()
            f.close()

    def getResponse(self, sentence):

        # perform spell check
        doc = self.nlp(sentence)
        if(doc._.performed_spellCheck):

            tokens = list(doc)
            suggestions = doc._.suggestions_spellCheck
            misspell_dict = {}
            for key in suggestions:
                misspell_dict[key.text] = suggestions[key]

            new_sentence = ''
            i = 0
            for word in sentence.split():
                stop = False
                if i >= len(tokens): stop = True

                while not stop:
                    if tokens[i].text in list(misspell_dict.keys()) and not tokens[i].text.casefold() in self.ignore_words:
                        new_sentence = new_sentence + misspell_dict[tokens[i].text]
                    else:
                        new_sentence = new_sentence + tokens[i].text
                            
                    i = i + 1
                    if i >= len(tokens) or not tokens[i].text in word: stop = True
                new_sentence = new_sentence + ' '

            sentence = new_sentence

        sentence = tokenize(sentence)
        X = bag_of_words(sentence, self.all_words)
        X = X.reshape(1, X.shape[0])
        X = torch.from_numpy(X).to(self.device)

        output = self.model(X)
        _, predicted = torch.max(output, dim=1)

        tag = self.tags[predicted.item()]

        probs = torch.softmax(output, dim=1)
        prob = probs[0][predicted.item()]
        if prob.item() > 0.75:
            for intent in self.intents['intents']:
                if tag == intent["tag"]:
                    response = random.choice(intent['responses'])
                    return response
        else:
            return "I do not understand..."