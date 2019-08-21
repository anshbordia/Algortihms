Extractive Text Summarization:

Outputs the most important sentences of an article based on a sentence scoring scheme.

The following implementations are used to tackle the problem:
1) Artificial Neural Network (ANN) - Gives the best accuracy. Use this to produce summaries of articles.
The next implementations were done to compare their performance against the Neural Network. So, they only output the accuracy their accuracy on the dataset, however, can be easily extended to output summaries.
2) Random Forest
3) Decision Tree
4) Support Vector Machine (SVM)
5) Naive Bayes
6) K-Nearest Neighbours (KNN)

Works best on newspaper articles as it is trained to summarise them.

Merge.csv is the a specially curated dataset for this task from many different newspaper articles (although I have only put a small fraction of it publicly).

Please see the attached published research paper summarization_paper.pdf for a detailed description of the project.

Simply download the files and run the python script ann.py. Give the article within double quotes.

Dependencies:
1) Python 3.6 (Could scale to other versions easily with minor changes)
2) Keras
3) NLTK
4) Numpy
5) Pandas
6) Sklearn
7) Tensorflow




