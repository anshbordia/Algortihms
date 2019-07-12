import pandas as pd
import numpy as np

dictionary = pd.read_csv("dict.txt",delimiter='\n')
misspell = pd.read_csv("misspell.txt", delimiter = '\n')
correct = pd.read_csv("correct.txt", delimiter = '\n')
dictionary.iloc[209461][0] = "null"
dictionary.iloc[196708][0] = "nan"

dictionary_list = []
for i in range(0, len(dictionary)):
    dictionary_list.append(dictionary.iloc[i][0])

actual_misspelt = []
for i in range(0, len(misspell)):
    word = misspell.iloc[i][0]
    word2 = correct.iloc[i][0]
    if((word not in dictionary_list) and (word2 in dictionary_list)):
        actual_misspelt.append(word)
            
def applyGED(word, pred):
    word_size = len(word)
    best_value = -100
    best_index = -1
    for x in range(0, len(pred)):
        element = pred[x]
        elem_size = len(element)    
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
        if(arr[elem_size][word_size] > best_value):
            best_value = arr[elem_size][word_size]
            best_index = x
    return pred[best_index] 

gedo_list = []
for i in range (0, 1):
    gedo_list.append(applyGED(actual_misspelt[i], dictionary))

    