# COSC 4P02 Chat Bot

## Table of Contents
1. Project Information
2. Project Setup

## Project Information

The ideology for this group project is for Professor Naser Ezzati-Jivan’s COSC 4P02 Software Engineering 2 Class @ Brock University (2022 Winter Term).
 
#### Title:

Chat Bot Application for Canada Games and Brock Universities Sakai

#### Website:

https://brock-chatbot.azurewebsites.net/

#### Team Name:

The Aftermath of Stack OverFlow
 
#### Members of Group:

Aaiyan Virji – 6844237  
Dhairya Jaiswal - 6292064  
Jay Deal - 4971982  
Kevin Xu – 6824163  
Mehtab Jalf – 6246219  
Riley VanDonge – 6496897  
Leader - Ryan Brandrick – 6497994  
 
#### Synopsis of Project:

We are aiming to create a chat bot which meets all of the functional requirements of the end user. This is being achieved by creating a friendly UI and bot which is able to understand user needs and respond accordingly. The bot will allow for the end user to ask inquiries and attain information within a few keystrokes.

## Project Setup

There are two options for installing the project.
1. Install the full blown Node JS app, allowing you to run the project as a website on your local computer.
2. Install just the Python component, allowing you to test the chatbot functionality without the UI.

### 1) Download
Download this repository.  
Navigate to the *nlp* folder.  

### 2) Install Node
Skip this step if you are not installing the full Node JS app. 
Installation packages can be found at https://nodejs.dev/download/.   

### 3) Install Necessary Python Libraries
First make sure Python 3.9 is your default Python installation.  
Other Python 3.x versions may work, but the website was only tested on Python 3.9.  
In any case, the Python 3.x installation must be the default.  

##### Create Python virtual environment (recommended)
```
python -m venv venv
```

##### Activate the virtual environment  
Mac/Linux:
```
. venv/bin/activate
```
Windows:
```
venv\Scripts\activate
```

##### Install the required libraries
```
pip install -r requirements.txt
```

### 4) Run the Code
##### If you are running the full Node website:
```
node app.js
```
Then go to ```localhost:xxxx``` in a web browser, where *xxxx* is the port number output in the console.

##### If you are just testing the functionality without the UI:
```
python chat.py question
```
where *question* is the question you would like to get a response to from the chatbot.
