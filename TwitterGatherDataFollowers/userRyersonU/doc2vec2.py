from gensim.test.utils import common_texts
from gensim.models.doc2vec import Doc2Vec, TaggedDocument
from sklearn.metrics import accuracy_score, f1_score
from sklearn.model_selection import train_test_split
from sklearn.metrics import confusion_matrix
from sklearn.linear_model import LogisticRegression
from sklearn.neural_network import MLPClassifier
import numpy as np
import pandas as pd
from sklearn import utils
import csv
from tqdm import tqdm
import multiprocessing
import nltk
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix, classification_report
from sklearn.manifold import TSNE

tqdm.pandas(desc="progress-bar")
# Function for tokenizing
def tokenize_text(text):
    tokens = []
    for sent in nltk.sent_tokenize(text):
        for word in nltk.word_tokenize(sent):
            if len(word) < 2:
                continue
            tokens.append(word.lower())
    return tokens
# Initializing the variables
train_documents = []
test_documents = []
i = 0
# Associating the tags(labels) with numbers
tags_index = {'HRBlockCanada': 1 , 'akimboart': 2, 'MykelJEstes': 3, 'USBCanada': 4, 'CBCcathyalex': 5, 'RyersonU': 6, 'HemingwayCats': 7, 'lucyheaps24': 8, 'MarshallJulius': 9, 'wichitaksgirl': 10, 'WeighLossDrinks': 11}
#print(tags_index.values())
key_list = list(tags_index.keys())
val_list = list(tags_index.values())
print(key_list[val_list.index(7)])
#Reading the file
FILEPATH = 'D:/python-progs/Reduced_57-april30.csv'
with open(FILEPATH, 'r') as csvfile:
 with open('D:/python-progs/Reduced_57-april30.csv', 'r') as csvfile:
    moviereader = csv.reader(csvfile, delimiter=',', quotechar='"')
    for row in moviereader:
        if i == 0:
            i += 1
            continue
        i += 1
        if i <= 2000:
            train_documents.append(TaggedDocument(words=tokenize_text(row[2]), tags=[tags_index.get(row[3], 8)]))
        else:
            test_documents.append(TaggedDocument(words=tokenize_text(row[2]), tags=[tags_index.get(row[3], 8)]))
print(train_documents[8])
print('printing tags for sepide:',train_documents[4].tags)

cores = multiprocessing.cpu_count()

model_dbow = Doc2Vec(dm=1, vector_size=200, negative=5, hs=0, min_count=2, sample = 0, workers=cores, alpha=0.025, min_alpha=0.001)
model_dbow.build_vocab([x for x in tqdm(train_documents)])
train_documents  = utils.shuffle(train_documents)
model_dbow.train(train_documents,total_examples=len(train_documents), epochs=30)
def vector_for_learning(model, input_docs):
    sents = input_docs
    targets, feature_vectors = zip(*[(doc.tags[0], model.infer_vector(doc.words, steps=20)) for doc in sents])
    return targets, feature_vectors
model_dbow.save('D:/python-progs/tweetModel.d2v')

y_train, X_train = vector_for_learning(model_dbow, train_documents)
y_test, X_test = vector_for_learning(model_dbow, test_documents)


logreg = LogisticRegression(n_jobs=1, C=1e5)
logreg.fit(X_train, y_train)
y_pred = logreg.predict(X_test)
#sepi = key_list[val_list.index((y_pred[715]))]
print("Sepide testing output of labels", key_list[val_list.index((y_pred[300]))])
df = pd.DataFrame({'Actual': y_test, 'Predicted': y_pred})
df
print(df)
print('Testing accuracy for movie plots LogisticRegression%s' % accuracy_score(y_test, y_pred))
print('Testing F1 score for movie plots LogisticRegression: {}'.format(f1_score(y_test, y_pred, average='weighted')))

# Search based on a specific tweetID, where the string is the TweetID
#similar_tweets = model_dbow.docvecs.most_similar('544515')
#print(similar_tweets)


mlp = MLPClassifier(hidden_layer_sizes=(150,100,50), max_iter=300,activation = 'relu',solver='adam',random_state=1)
mlp.fit(X_train, y_train)
y_pred = mlp.predict(X_test)
print("Sepide testing output of labels", key_list[val_list.index((y_pred[3]))])
#df = pd.DataFrame({'Actual': y_test, 'Predicted': y_pred})
#print(df)
#print(mlp.predict_proba(y_pred[2]))
#print(confusion_matrix(y_train, y_train))
#print(clf.predict(y_pred[7]))


print('Testing accuracy for movie plots MLPClassifier%s' % accuracy_score(y_test, y_pred))
print('Testing F1 score for movie plots MLPClassifier: {}'.format(f1_score(y_test, y_pred, average='weighted')))

# plotting function to visualize the results of the classifier. 

#def heatconmat(y_true,y_pred):
    #sns.set_context('talk')
    #plt.figure(figsize=(9,6))
    #sns.heatmap(confusion_matrix(y_true,y_pred),
                #annot=True,
                #fmt='d',
                #cbar=False,
                #cmap='gist_earth_r',
                #yticklabels=sorted(y_test))
    #plt.show()
    #print(classification_report(y_true,y_pred))
#Fit and predict
#lrc = LogisticRegression(C=5, multi_class='multinomial', solver='saga',max_iter=1000)
#lrc.fit(X_train,y_train)
#y_pred = lrc.predict(X_test)
#heatconmat(y_test,y_pred)

# Implementing TSNE by Sepide

doc_tags = list(model_dbow.docvecs.doctags.keys())
print(doc_tags)
print(model_dbow.docvecs.vectors_docs)

#X = model_dbow[doc_tags]
#print(X)



#tsne = TSNE(n_components=2)
#X_tsne = tsne.fit_transform(X)
#df = pd.DataFrame(X_tsne, index=doc_tags, columns=['x', 'y'])

#plt.scatter(df['x'], df['y'], s=0.4, alpha=0.4)

#post the error on some stack overflow community 
 
					

