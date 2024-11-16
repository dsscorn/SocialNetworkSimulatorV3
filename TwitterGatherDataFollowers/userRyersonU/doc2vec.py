import pandas as pd
import multiprocessing
import numpy as np
from tqdm import tqdm
tqdm.pandas(desc="progress-bar")
from gensim.models import Doc2Vec
from gensim.models.doc2vec import Doc2Vec
from gensim.models import Doc2Vec, Phrases
from sklearn import utils
from sklearn.model_selection import train_test_split
import gensim
from sklearn.linear_model import LogisticRegression
from gensim.models.doc2vec import TaggedDocument
from gensim.parsing.preprocessing import STOPWORDS as stop_words
from gensim.utils import simple_preprocess
from sklearn.feature_extraction import text
from nltk.stem.lancaster import LancasterStemmer
from nltk.stem import SnowballStemmer
import re
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.metrics import accuracy_score, f1_score
from sklearn.neural_network import MLPClassifier
from sklearn.manifold import TSNE
from IPython.display import display
from time import time 
import sys
from pprint import pprint, pformat
import csv
import codecs
import gc
import itertools
import os
import time
from gensim.models import doc2vec
from sklearn.linear_model import LogisticRegression
import pickle
from gensim.models import KeyedVectors
import matplotlib.pyplot as plt

# Set the OPENBLAS_NUM_THREADS environment variable
os.environ['OPENBLAS_NUM_THREADS'] = '8'


# Get the value of the OPENBLAS_NUM_THREADS environment variable
num_threads = os.environ.get('OPENBLAS_NUM_THREADS')


usersRec = str(sys.argv[1]).strip('')
topRec = int(sys.argv[2])
FileName = sys.argv[3]
numNodes = str(int(sys.argv[4]))

time_sec_start = time.time()

#Converting a txt file to a csv file
txtfile = FileName  #commeneted out on Oct.29
path = "Dataset/424k/"

csv_file = path + "mynew"+ numNodes + ".csv"
print("csv path" , csv_file) 
#csv_file = r"D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv"  #commenetd out on Oct29
#csv_file = "D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv"
#csv_file2 = r"D:/Simulator-S-15-May-2020/Dataset/424k/MyNew-2.csv"
#in_txt = csv.reader(open(txtfile, "rb"), delimiter = '\t')
#out_csv = csv.writer(open(csv_file, 'wb'))
#out_csv.writerows(in_txt)


with open(txtfile, 'r') as infile, open(csv_file, 'w') as outfile:
     stripped = (line.strip() for line in infile)
     lines = (line.split("\t") for line in stripped if line)
     writer = csv.writer(outfile)
     writer.writerows(lines)
	
infile.close()
outfile.close()	


#commented out on Oct.29
#csv_file = r"D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv"  #commented out on Nov.3 
	 
#Set a header for the csv file added on Oct. 7
#df = pd.read_csv(csv_file, header=None)
#df.columns = ['Followee', 'ID', 'Date', 'UserID', 'Follower', 'Tweets']
#df.to_csv(r'D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv', index=False, mode='w')  #commented out on Nov. 3
#df.to_csv(csv_file, index=False, mode='w')


#Set a header for a csv file added on Oct. 13
#HeaderToWrite = "Followee,ID,Date,UserID,Follower,Tweets\n"  #commeneted on Oct. 13

# Check if the environment variable is set
if num_threads is not None:
    print(f"OpenBLAS is configured to use {num_threads} threads")
else:
    print("The OPENBLAS_NUM_THREADS environment variable is not set")

#Set a header for the csv file
read_file = pd.read_csv(csv_file, header=None) 
#read_file = pd.read_csv (out_csv)
#headerList = ['Followee', 'ID', 'Date', 'UserID', 'Follower', 'Tweets'] 
headerList = ['Tweets', 'Follower']  
#read_file.to_csv(r'D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv', header=headerList, index=False, mode='w')
read_file.to_csv(csv_file, header=headerList, index=False, mode='w')    


# open CSV file and assign header
#with open("D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv",'w',newline='') as file:
     #writer = csv.writer(file)
     #writer.writerow(headerList)
     #dw = csv.DictWriter(file, delimiter=',', 
                        #fieldnames=headerList)
     #dw.writeheader()

#read_file = pd.read_csv(csv_file)
	 
#df = pd.read_csv('D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv') #commented out on Oct. 13
#df = pd.read_csv('D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv')  #commented out on Oct. 29
df = pd.read_csv(csv_file)


df = df[['Tweets','Follower']]
df = df[pd.notnull(df['Tweets'])]
df.rename(columns = {'Tweets':'Tweets'}, inplace = True)
df.head(5)  # it was 10 and I changed it to 20
display(df.head(5))
print(df.shape)


# get the number of rows for input dataset
#file = open("D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv")  #commented out on Oct. 20
#reader_file = csv.reader(file)  #commented out on Oct. 20

#reader_file = pd.read_csv(csv_file)  #commenetd out on Oct. 22
input_file = open(csv_file,"r+")
#input_file.close()
reader_file = csv.reader(input_file)
value = len(list(reader_file))
#df.index = range(57762)
print("value: " , value)
df.index = range(value - 1) 

#df = pd.read_csv('D:/Simulator-S-15-May-2020/Dataset/424k/MyNew.csv')  #commented out on Oct. 29
df = pd.read_csv(csv_file)  #commented out on Nov. 19
#df['All Tweets'] = df.groupby('Followee')['Tweets'].\transform(lambda x: ''.join(x))
#df['All-Tweets'] = df[['Followee','ID','Date', 'UserID','Follower', 'Tweets']].groupby('Follower')['Tweets'].transform(lambda x: ' '.join(x))

#convert CSV file to Pickle format
pickle_file = path + "mynew"+ numNodes + ".pkl"						   
df.to_pickle(pickle_file)

cleaned_tweets_df = pd.read_pickle(pickle_file)

look_up_file = path + numNodes + "_lookup_dict.pickle"

def read_pickle_create_lookup(pickle_file):
    #this takes in the joined_text_df pickle file to create a lookup dict for the model
    df_joined = pd.read_pickle(pickle_file)
    lookup_df = df_joined[['Tweets', 'Follower']]
    lookup_df['Follower'] = lookup_df['Follower'].astype(str)
    lookup_df = lookup_df.set_index('Follower')
    lookup_df = lookup_df.loc[~lookup_df.index.duplicated(keep='first')]  #added on July 2023 
    lookup_dict = lookup_df.to_dict(orient='index')
    return lookup_dict
	
with open(look_up_file, 'wb') as f:
    # Pickle the 'data' dictionary using the highest protocol available.
    pickle.dump(read_pickle_create_lookup(pickle_file), f, pickle.HIGHEST_PROTOCOL)

# load lookup dictoinary pickle file
file = open(look_up_file, 'rb')
lookup_dict = pickle.load(file)
file.close


def make_stop_words():
    global stop_words
    letters = list('abcdefghijklmnopqrstuvwxyz')
    numbers = list('0123456789')
    words = ['a', 'able', 'about', 'above', 'abst', 'accordance', 'according',\
         'actually', 'added', 'affected', 'affecting', 'another', 'anybody', 'anyhow',\
         'around', 'back', 'became', 'because', 'become', 'beginnings', 'cant']
    stopwords = stop_words.union(set(letters)).union(set(numbers)).union(set(words))
    
    my_stop_words = text.ENGLISH_STOP_WORDS.union(stopwords)
    return my_stop_words
	
def preprocessor_and_stem(text, my_stop_words):
    """uses gensim simple_preprocess and then removes stop words
    -> used in the tag_docs function
    """
    # Instantiate a LancasterStemmer object, use gensim simple_preprocess to tokenize/lowercase
    # and then removes stop words
    ls = LancasterStemmer()
    simple = simple_preprocess(text)
    result = [ls.stem(word) for word in simple if not word in my_stop_words]
    return result

def stem_tag_docs(docs, my_stop_words):
    ls = LancasterStemmer()
    results = docs.apply(lambda r: TaggedDocument(words=preprocessor_and_stem(r['Tweets'], my_stop_words), tags=[str(r['Follower'])]), axis=1)
    return results.tolist()	

my_stop_words = make_stop_words()

# initiate a lancaster stemmer, and stem beer reviews while tagging them for Doc2Vec
ls = LancasterStemmer()
tagged_stem_docs = stem_tag_docs(cleaned_tweets_df, my_stop_words)

cores = multiprocessing.cpu_count()
print("cores", cores)

# Instantiate a Doc2Vec model, and build the vocab from the tagged documents

model = Doc2Vec(dm=0, dbow_words=1, min_count=4, negative=3,
                hs=0, sample=1e-4, window=5, vector_size=300, workers=4)

model.build_vocab(tagged_stem_docs, progress_per = 100)

model.train(tagged_stem_docs, total_examples=model.corpus_count, epochs=20)

""" #commented out on Nov. 25
print(df['Tweets'].apply(lambda x: len(x.split(' '))).sum())

cnt_pro = df['Follower'].value_counts()
plt.figure(figsize=(12,4))
sns.barplot(x=cnt_pro.index,y=cnt_pro.values, alpha=0.8)
plt.ylabel('Number of Occurrences', fontsize=12)
plt.xlabel('Follower', fontsize=4)
plt.xticks(rotation=90)
#plt.show()


def print_tweet(index):
    example = df[df.index == index][['Tweets', 'Follower']].values[0]
    if len(example) > 0:
        print(example[0])
        print('Follower:', example[1])
#print_tweet(3)  #commented out on Nov. 17
#print_tweet(2)

#pre-processing steps 
from bs4 import BeautifulSoup
def cleanText(text):
    text = BeautifulSoup(text, "lxml").text
    text = re.sub(r'\|\|\|', r' ', text) 
    text = re.sub(r'http\S+', r'<URL>', text)
    text = text.lower()
    #text = text.replace('x', '')
    return text
#df['Follower'] = df['Follower'].apply(cleanText)
df['Tweets'] = df['Tweets'].apply(cleanText)
df['Tweets'][1]
#print("Test after Cleaning-Step1")
#train_size=57763
"""

"""
def label_sentences(corpus, label_type):
    
    #Gensim's Doc2Vec implementation requires each document/paragraph to have a label associated with it.
    #We do this by using the TaggedDocument method. The format will be "TRAIN_i" or "TEST_i" where "i" is
    #a dummy index of the complaint narrative.

    labeled = []
    for i, v in enumerate(corpus):
        label = label_type + '_' + str(i)
        labeled.append(doc2vec.TaggedDocument(v.split(), [label]))
    return labeled

"""	
train, test = train_test_split(df, train_size=0.999, test_size=0.001, random_state=42)
train, test = train_test_split(df, train_size=0.9999, test_size=0.0001, random_state=42)
"""
X_train, X_test, y_train, y_test = train_test_split(df.Tweets, df.Follower, random_state=42, train_size=0.9999, test_size=0.0001)
X_train = label_sentences(X_train, 'Train')
X_test = label_sentences(X_test, 'Test')
all_data = X_train + X_test
print("len: ", len(all_data))
print(all_data[:2])
"""

  #commented out on Nov. 25
import nltk
from nltk.corpus import stopwords
def tokenize_text(text):
    tokens = []
    for sent in nltk.sent_tokenize(text):
        for word in nltk.word_tokenize(sent):
            if len(word) < 2:
                continue
            tokens.append(word.lower())
    return tokens

	
#print("Test after Cleaning-Step2")
#gc.collect()  //commented out on Nov. 19

#tweet_ID = list(df['ID'])
#tweets = list(df['Tweets'])

#train_tagged = train.apply(
    #lambda r: TaggedDocument(words=tokenize_text(r['Tweets']), tags=[r.Follower]), axis=1)
#test_tagged = test.apply(
    #lambda r: TaggedDocument(words=tokenize_text(r['Tweets']), tags=[r.Follower]), axis=1)

 #commented out on Nov 19	
#changed the train_tags and test_tags to have a key on October 5th 	

#commented out on Nov. 25
train_tagged = train.apply(
    lambda r: TaggedDocument(words=tokenize_text(r['Tweets']), tags=[r.Follower]), axis=1)
test_tagged = test.apply(
    lambda r: TaggedDocument(words=tokenize_text(r['Tweets']), tags=[r.Follower]), axis=1)

#print("Train tagged")
#print("train tags" , train_tagged.values[5])
#print("test tags" , test_tagged.values[0])
#print(train_tagged.values[usersRec])
#print("Test after Cleaning-Step3")
""" #commented out on Nov. 25
max_epochs = 30  #was 30 I changed it to 10
vec_size = 200
alpha = 0.025
"""
""" #commented out on Nov. 25
cores = multiprocessing.cpu_count()
#model_dbow = Doc2Vec(dm=1, vector_size=300, negative=5, hs=0, min_count=2, sample = 0, workers=cores)
model_dbow = Doc2Vec(dm=0, vector_size=300, negative=5, hs=0, min_count=2, sample = 0, workers=cores)
model_dbow.build_vocab([x for x in tqdm(train_tagged.values)])  #commented out on Nov. 19
#model_dbow.build_vocab([x for x in tqdm(test_tagged.values)])
#model_dbow.build_vocab([x for x in tqdm(all_data)])

"""

""" #commented out on Nov. 25
for epoch in range(max_epochs):
    print('iteration {0}'.format(epoch))
    model_dbow.train(train_tagged,
                total_examples=model_dbow.corpus_count,
                epochs=model_dbow.epochs)
    #decrease the learning rate
    model_dbow.alpha -= 0.0002
    #fix the learning rate, no decay
    model_dbow.min_alpha = model_dbow.alpha
	
"""
	
""" #commenetd out on Oct. 22

 #commented out on Nov. 19	
for epoch in range(30):
    model_dbow.train(utils.shuffle([x for x in tqdm(train_tagged.values)]), total_examples=len(train_tagged.values), epochs=1)
    model_dbow.alpha -= 0.002
    model_dbow.min_alpha = model_dbow.alpha

def vec_for_learning(model, tagged_docs):
    sents = tagged_docs.values
    targets, regressors = zip(*[(doc.tags[0], model.infer_vector(doc.words, epochs=None)) for doc in sents])
    return targets, regressors

y_train, X_train = vec_for_learning(model_dbow, train_tagged)
y_test, X_test = vec_for_learning(model_dbow, test_tagged)

logreg = LogisticRegression(n_jobs=1, C=1e5, max_iter=10000)
logreg.fit(X_train, y_train)
y_pred = logreg.predict(X_test)

from sklearn.metrics import accuracy_score, f1_score

print('Testing accuracy %s' % accuracy_score(y_test, y_pred))
print('Testing F1 score: {}'.format(f1_score(y_test, y_pred, average='weighted')))
"""

""" 

#%%time
for epoch in range(30):
    model_dbow.train(utils.shuffle([x for x in tqdm(all_data)]), total_examples=len(all_data), epochs=1)
    model_dbow.alpha -= 0.002
    model_dbow.min_alpha = model_dbow.alpha
	
"""
"""	
for epoch in range(max_epochs):
    print('iteration {0}'.format(epoch))
    model_dbow.train(tagged_data,
                total_examples=model_dbow.corpus_count,
                epochs=model_dbow.epochs)
    # decrease the learning rate
    model_dbow.alpha -= 0.0002
    # fix the learning rate, no decay
    model_dbow.min_alpha = model_dbow.alpha
	
	
	
"""	

 
path2 = "Stored_Doc2VecModel/"
	
#model_dbow.save("D:/Simulator-S-15-May-2020/TwitterGatherDataFollowers/userRyersonU/d2v.model")
model.save(path2 + "md"+ numNodes +"_d2v.model")
#model_dbow.save("D:/Jorge/Simulator-S-15-May-2020/Stored_Doc2VecModel/" + "md"+ numNodes +"_d2v.model")
print("Model Saved")

#print(model_dbow.docvecs.count)

#model= Doc2Vec.load("D:/Simulator-S-15-May-2020/TwitterGatherDataFollowers/userRyersonU/d2v.model")
model= Doc2Vec.load(path2 +"md" + numNodes +"_d2v.model")

#print(model.dv[usersRec])

#new_vector = model.infer_vector(docs)

#new_vector = model.infer_vector((TaggedDocument(words, tags=['HRBlockCanada']).split())

# the topn number comes from the Java code
 
similar_doc = model.dv.most_similar([model.dv[usersRec]],topn = topRec)
print(similar_doc)

#added on Sep. 30
#similar_doc = model.dv.most_similar('92842008')
#print("similar tweets to userID:9284008")
#print(similar_doc)

path3 = "important-stuff/"
fileName = path3 + numNodes + "_followeeRec.txt"

for i in range(topRec):
    for char in str(similar_doc[i]):
         if char in "(":
             str(similar_doc[i]).replace(char,'') 		 
    print("python", similar_doc[i])
    #sys.argv[1] = similar_doc[i]            

"""
with open (fileName, 'w') as f:
            for i in range(topRec):
                for char in str(similar_doc[i]):
                  if char in ")":
                      str(similar_doc[i]).replace(char,'')
					#similar_doc[i] = str(similar_doc[i]).translate(None, ')(')
					#similar_doc[i] = str(similar_doc[i]).translate(str(similar_doc[i]).maketrans('','',')('))
                if((similar_doc[i])[0] == usersRec):
                           print("topRec", topRec)
						   #similar_doc.remove((similar_doc[i]))
						   #topRec+=1
						   #print("topRec", topRec)
						   
                f.write(str(similar_doc[i])+"\n")
f.close()
"""
		
time_sec_end = time.time()
completionTime = time_sec_end - time_sec_start
print("completionTime", completionTime * 1000)

model= Doc2Vec.load(path2 +"md" + numNodes +"_d2v.model")

if hasattr(model, 'doctags'):
    # Perform actions when the attribute is present
    print("The model has the 'doctags' attribute")
else:
    # Perform actions when the attribute is not present
    print("The model does not have the 'doctags' attribute")
	
	
# Assuming 'model' is your trained Doc2Vec model
#doc_vectors = [model.wv.get_vector(tag) for tag in model.dv.index_to_key]

#doc_vectors = [np.mean([model.wv.get_vector(word) for word in document], axis=0) for document in tagged_stem_docs]

	
#doc_vectors = model.dv.vectors_docs
#tsne = TSNE(n_components=2, random_state=42)
#tsne_vectors = tsne.fit_transform(doc_vectors)

#plt.scatter(tsne_vectors[:, 0], tsne_vectors[:, 1])
#plt.show()

#doctags = model.dv.doctags

#implementation of t-SNE 
#doc_tags = list(model.dv.doctags.keys())

# get the vector for each doc_tag
#X = model[doc_tags]

# Fit and transform a t-SNE object with the vector data for dimensionality reduction
#tsne = TSNE(n_components=2)
#X_tsne = tsne.fit_transform(X)
#df = pd.DataFrame(X_tsne, index=doc_tags, columns=['x', 'y'])


#implementation of MLP
"""
def get_vectors(model, input_docs):
    sents = input_docs
    targets, feature_vectors = zip(*[(doc.tags[0], model.infer_vector(doc.words)) for doc in sents])
    return targets, feature_vectors
	
y_train, X_train = get_vectors(model, train_tagged)
y_test, X_test = get_vectors(model, test_tagged)

mlp = MLPClassifier(hidden_layer_sizes=(150,100,50), max_iter=1000,activation = 'relu',solver='adam',random_state=1)
mlp.fit(X_train, y_train)
y_pred = mlp.predict(X_test)

print("Sepide testing output of labels", y_pred[0])

print('Testing accuracy for movie plots MLPClassifier%s.', accuracy_score(y_test, y_pred))
print('Testing F1 score for movie plots MLPClassifier: {}',format(f1_score(y_test, y_pred, average='weighted')))


print('========Similarity after MLP===============')
print("Similarity after MLP",model.dv.most_similar(positive=[y_pred[0]]))
"""







