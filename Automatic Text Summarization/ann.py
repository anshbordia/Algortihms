# Extractive Summarization using ANN

# Import Libraries

import nltk
import numpy as np
#import matplotlib.pyplot as plt
import pandas as pd
import re
import collections
import operator
#import pickle
from nltk.tokenize import word_tokenize
from nltk.tokenize import sent_tokenize
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
import keras
from keras.models import Sequential
from keras.layers import Dense


# Standard Lists


keywords = ['conclusions', 'results', 'therefore', 'thus', 'hence', 'implies that', 'consequently',
            'proves that', 'as a result', 'indicates that', 'suggests that', 'in conclusion',
            'it follows that', 'accordingly', 'conclude', 'the conclusion that', 'because',
            'given that','so', 'may be deduced from']

non_essential = ['moreover', 'furthermore', 'in addition', 'secondly', 'moving forward', 'besides',
                 'on the other hand', 'whereas', 'plus', 'notwithstanding']


# User enters text

text = input("Enter the text you wish to summarize: ")

# Preprocessing

sentence = sent_tokenize(text)
p2 = text

stop_words = stopwords.words('english')
ps = PorterStemmer()

store = []

myDict = {}
p2 = p2.lower()
p2 = re.sub('[^a-zA-Z]', ' ', p2)
p2 = word_tokenize(p2)
p2 = [word for word in p2 if not word in set(stop_words)]
p2 = [ps.stem(word) for word in p2]
#p2 = ' '.join(p2)
for word in p2:
    if (word in myDict):
        myDict[word] = myDict[word] + 1
    else:
        myDict[word] = 1
 
od = collections.OrderedDict(sorted(myDict.items())) 
sorted_dict = sorted(myDict.items(), key=operator.itemgetter(1))      
no_unique = len(sorted_dict)

freq_words = []
for i in range(no_unique - 5,no_unique):
    freq_words.append(sorted_dict[i][0])


no_of_sent = len(sent_tokenize(text))

no_of_freq = len(freq_words)

len_keywords = len(keywords)
len_ns = len(non_essential)

corp = np.zeros((no_of_sent,6))

# Matrix of Features Formation

for i in range(0, no_of_sent):
    p22 = sentence[i]
    store = nltk.pos_tag(word_tokenize(p22))
    temp = sentence[i]
    temp = re.sub('[^a-zA-Z0-9]', ' ', temp)
    temp = word_tokenize(temp.lower())
    temp = [word for word in temp if not word in set(stop_words)]
    temp = [ps.stem(word) for word in temp]
    a = len(temp)
    for word in temp:
        if word in set(freq_words):  
            corp[i][0] = corp[i][0] + 1
    temp = ' '.join(temp)    
    for k in range(0, len_keywords):
        if keywords[k] in temp:
            corp[i][1] = 1
            break
    if a <= 15:
        corp[i][2] = 1
    if i == 0 or i == no_of_sent-1:
        corp[i][3] = 1
    corp[i][4] = 1    
    for k in range(0, len_ns):
        if non_essential[k] in temp:
            corp[i][4] = 0
            break    
    for cell in store:
        if cell[1] == 'NNP' :
            corp[i][5] = 1
            break


# Applying classification algorithm

dataset = pd.read_csv('merge.csv')
X = dataset.iloc[:, [0,1,2,3,4,5]].values
y = dataset.iloc[:, 6].values

# Splitting the dataset into the Training set and Test set
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.20, random_state = 0)

# Feature Scaling
sc = StandardScaler()
X_train = sc.fit_transform(X_train)
X_test = sc.transform(X_test)

# Fitting training set to ANN

classifier = Sequential()

classifier.add(Dense(output_dim = 3, init = 'uniform', activation = 'relu', input_dim = 6))
classifier.add(Dense(output_dim = 3, init = 'uniform', activation = 'relu'))
classifier.add(Dense(output_dim = 1, init = 'uniform', activation = 'sigmoid'))

classifier.compile(optimizer = 'adam', loss = 'binary_crossentropy', metrics = ['accuracy'])

classifier.fit(X_train, y_train, batch_size = 10, nb_epoch = 100)

# Predicting the Test set results
y_pred = classifier.predict(corp)
y_predtr = classifier.predict(X_test)


""""from sklearn.model_selection import cross_val_score
accuracies = cross_val_score(estimator = classifier, X = X_train, y = y_train, cv = 10)
meank = accuracies.mean()
stdk = accuracies.std()"""

for i in range(0, len(y_pred)):
    if y_pred[i] == 1:
        print(sentence[i])

counter = 0
for number in y_pred:
    if number == 1:
        counter = counter + 1
sent_num = 0
if counter == 0:
    score = []
    
    for record in corp:
        scr = 0.5 * record[0] + 1.5 * record[1] + 0.2 * record[2] + 0.5 * record[3] + 0.3 * record[4] + 0.5 * record[5] 
        score.append(scr)
            
    if(len(score) < 5):
        print(sentence[score.index(max(score))])
    elif(len(score) < 10):
        print(sentence[score.index(max(score))])
        score[score.index(max(score))] = -1
        print(sentence[score.index(max(score))])
    else:
        print(sentence[score.index(max(score))])
        score[score.index(max(score))] = -1
        print(sentence[score.index(max(score))])
        score[score.index(max(score))] = -1
        print(sentence[score.index(max(score))])
