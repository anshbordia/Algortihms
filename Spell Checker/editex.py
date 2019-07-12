#Modified GED

import pandas as pd
import numpy as np

dictionary = pd.read_csv("dict.txt",delimiter='\n')
misspell = pd.read_csv("misspell.txt", delimiter = '\n')
correct = pd.read_csv("correct.txt", delimiter = '\n')
dictionary.iloc[209461][0] = "null"
dictionary.iloc[196708][0] = "nan"

groups = [['a', 'e', 'i', 'o', 'u', 'y'], ['b', 'p'], ['c', 'k', 'q'],
         ['d', 't'], ['l', 'r'], ['f', 'p', 'v'], ['s', 'x', 'z'],
         ['c', 's', 'z'], ['m', 'n'], ['g', 'j']]

dictionary_list = []
for i in range(0, len(dictionary)):
    dictionary_list.append(dictionary.iloc[i][0])

actual_misspelt = []
correct_vector = []
for i in range(0, len(misspell)):
    word = misspell.iloc[i][0]
    word2 = correct.iloc[i][0]
    if((word not in dictionary_list) and (word2 in dictionary_list)):
        actual_misspelt.append(word)
        correct_vector.append(word2)
    
best_matches = []    
def applyGED(word, dictionary):
    word_size = len(word)
    best_value = -100
    for x in range(0, len(dictionary)):
        element = dictionary.iloc[x][0]
        elem_size = len(element)    
        arr = np.zeros((elem_size + 1, word_size + 1))
        for i in range(1, elem_size + 1):
            for j in range(0, 1):
                arr[i][j] = -i * 2
        for i in range(0, 1):
            for j in range(1, word_size + 1):
                arr[i][j] = -j * 2
        for i in range(1, elem_size + 1):
            for j in range(1, word_size + 1):
                insert_cost = arr[i - 1][j]
                delete_cost = arr[i][j - 1] - 2
                if(word[j - 1] == element[i - 1]):
                    diagonal_cost = arr[i - 1][j - 1] + 2
                else:
                    val = checkgroups(word[j - 1], element[i - 1])
                    diagonal_cost = arr[i - 1][j - 1] - val
                arr[i][j] = max(insert_cost, delete_cost, diagonal_cost)
        if(arr[elem_size][word_size] > best_value):
            best_matches = []
            best_value = arr[elem_size][word_size]
            best_matches.append([dictionary.iloc[x][0], arr[elem_size][word_size]])
        elif(arr[elem_size][word_size] == best_value):
            best_matches.append([dictionary.iloc[x][0], arr[elem_size][word_size]])
    return best_matches

ged_list = []
for i in range(0, 1):
    ged_list.append(applyGED(actual_misspelt[i], dictionary))

count = 0
for i in range(0, ):
    if(correct_vector[i] in ged_list[i]):
        count += 1
acc = count/len(correct_vector)


def checkgroups(a, b):
    if(a in groups[0] and b in groups[0]):
        return -0.5
    elif(a in groups[1] and b in groups[1]):
        return -0.5
    elif(a in groups[2] and b in groups[2]):
        return -0.5
    elif(a in groups[3] and b in groups[3]):
        return -0.5
    elif(a in groups[4] and b in groups[4]):
        return -0.5
    elif(a in groups[5] and b in groups[5]):
        return -0.5
    elif(a in groups[6] and b in groups[6]):
        return -0.5
    elif(a in groups[7] and b in groups[7]):
        return -0.5
    elif(a in groups[8] and b in groups[8]):
        return -0.5
    elif(a in groups[9] and b in groups[9]):
        return -0.5
    else:
        return -2