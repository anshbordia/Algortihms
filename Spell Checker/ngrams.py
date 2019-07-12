# Spell Correction using N-Grams
import pandas as pd
from nltk.util import ngrams
import numpy as np

dictionary = pd.read_csv("dict.txt",delimiter='\n')
misspell = pd.read_csv("misspell.txt", delimiter = '\n')
correct = pd.read_csv("correct.txt", delimiter = '\n')
dictionary.iloc[209461][0] = "null"
dictionary.iloc[196708][0] = "nan"

ngram_list = []
for i in range(0, len(dictionary)):
    ngram_list.append(list(ngrams(dictionary.iloc[i][0], 2)))
    
dictionary_list = []
for i in range(0, len(dictionary)):
    dictionary_list.append(dictionary.iloc[i][0])
    
actual_misspelt = []
correct_vector = []
for i in range(0, len(misspell)):
    word = misspell.iloc[i][0]
    word2 = correct.iloc[i][0]
    if((word not in dictionary_list) and (word2 in dictionary_list)):
        if word == '2':
            word = 'to'
        elif word == '4':
            word = 'for'
        actual_misspelt.append(word)
        correct_vector.append(word2)

best_matches = []      
def ngramscalc(word, ngram_list):
    distt = []
    best_matches = []
    x = list(ngrams(word,2))
    temp = x.copy()
    x_len = len(x)
    min_distance = 100
    for i in range(0, len(ngram_list)):
        y_len = len(ngram_list[i])
        common_count = findCommon(temp, ngram_list[i])
        distance = (x_len + y_len) - (2 * common_count)
        distt.append(distance)
        if distance < min_distance:
            best_matches = []
            min_distance = distance
            best_matches.append(dictionary.iloc[i][0])
        elif distance == min_distance:
            best_matches.append(dictionary.iloc[i][0])
    return best_matches

def findCommon(x, y):
    common_count = 0
    i = 0
    temp = y.copy()
    while(i < len(x)):
        if x[i] in temp:
            common_count += 1
            temp.remove(x[i])
        i += 1
    return common_count

predictions = []
for word in actual_misspelt:
    predictions.append(ngramscalc(word, ngram_list))
    
count = 0
ctz = 0
for i in range(0, len(correct_vector)):
    if(correct_vector[i] in predictions[i]):
        count += 1
        tmp = 1 / len(predictions[i])
        ctz += tmp
acc = count/len(correct_vector)
ctz /= 351

def applyGED(word, predictions):
    word_size = len(word)
    best_value = -100
    best_index = -1
    for x in range(0, len(predictions)):
        element = predictions[x]
        elem_size = len(element)    
        if(elem_size > 4):
            arr = np.zeros((elem_size + 1, word_size + 1), dtype=np.int)
            for i in range(1, elem_size + 1):
                for j in range(0, 1):
                    arr[i][j] = -i
            for i in range(0, 1):
                for j in range(1, word_size + 1):
                    arr[i][j] = -j
            for i in range(1, elem_size + 1):
                for j in range(1, word_size + 1):
                    insert_cost = arr[i - 1][j] - 1
                    delete_cost = arr[i][j - 1] - 1
                    if(word[j - 1] == element[i - 1]):
                        diagonal_cost = arr[i - 1][j - 1] + 1
                    else:
                        diagonal_cost = arr[i - 1][j - 1] - 1
                    arr[i][j] = max(insert_cost, delete_cost, diagonal_cost)
            if(arr[elem_size][word_size] >= best_value):
                best_value = arr[elem_size][word_size]
                best_index = x
        else:
            arr = np.zeros((elem_size + 1, word_size + 1), dtype=np.int)
            for i in range(1, elem_size + 1):
                for j in range(0, 1):
                    arr[i][j] = -i
            for i in range(0, 1):
                for j in range(1, word_size + 1):
                    arr[i][j] = -j
            for i in range(1, elem_size + 1):
                for j in range(1, word_size + 1):
                    insert_cost = arr[i - 1][j] - 1
                    delete_cost = arr[i][j - 1] - 1
                    if(word[j - 1] == element[i - 1]):
                        diagonal_cost = arr[i - 1][j - 1] + 2
                    else:
                        diagonal_cost = arr[i - 1][j - 1] - 3
                    arr[i][j] = max(insert_cost, delete_cost, diagonal_cost)
            if(arr[elem_size][word_size] >= best_value):
                best_value = arr[elem_size][word_size]
                best_index = x
    return predictions[best_index] 

gedo_list = []
for i in range (0, len(correct_vector)):
    gedo_list.append(applyGED(actual_misspelt[i], predictions[i]))
    
count2 = 0
for i in range(0, len(correct_vector)):
    if(correct_vector[i] in gedo_list[i]):
        count2 += 1
acc2 = count2/len(correct_vector)



