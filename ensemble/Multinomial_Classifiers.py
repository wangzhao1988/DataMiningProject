
import numpy as np
import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import scale
from sklearn.metrics import accuracy_score
from sklearn.linear_model import LogisticRegression
from sklearn import tree
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier, AdaBoostClassifier, ExtraTreesClassifier
from sklearn.svm import SVC
from sklearn.neighbors import KNeighborsClassifier
from sklearn.calibration import CalibratedClassifierCV
from sklearn.cross_validation import train_test_split
from sklearn.linear_model import LogisticRegressionCV
from sklearn.naive_bayes import MultinomialNB
from sklearn import decomposition


input_file = "/Users/zhao/Documents/CS6220/project/ensemble/preprocess.csv"

df_train = pd.read_csv(input_file, header = 0)
original_headers = list(df_train.columns.values)
labels = df_train['destination'].values
df_train = df_train.drop(['destination'], axis=1)
encoder = LabelEncoder()
labels = encoder.fit_transform(labels)
cat_field = ['booking2CreateLag', 'booking2ActiveLag', 'gender', 'signupMethod', 'signupFlow', 'language', 'channel',
             'provider', 'tracked', 'signupApp', 'deviceType', 'browser']

# df_train['age'] = scale(df_train['age'])
for f in cat_field:
    df_train[f] = encoder.fit_transform(df_train[f])

pca = decomposition.PCA(n_components=8)
pca.fit(df_train)
df_train = pca.transform(df_train)


random_state = 1
X, X_test, y, y_test = train_test_split(df_train, labels, test_size=0.2,
                                        random_state=random_state)

#Spliting train data into training and validation sets.
# X_train, X_valid, y_train, y_valid = train_test_split(X, y, test_size=0.2,
#                                                       random_state=random_state)

#Defining the classifiers
clfs = {
        # 'LR'  : LogisticRegression(random_state=random_state),
        # 'SVM' : SVC(probability=True, random_state=random_state),
        'RF'  : RandomForestClassifier(n_estimators=100, n_jobs=-1,
                                       random_state=random_state),
        'GBM' : GradientBoostingClassifier(n_estimators=50,
                                           random_state=random_state),
        'DT'  : tree.DecisionTreeClassifier(criterion="entropy"),
        'ADA' : AdaBoostClassifier(tree.DecisionTreeClassifier(criterion="entropy"), n_estimators=50,
                                   random_state=random_state),
        # 'NB' : MultinomialNB(),
        # 'ETC' : ExtraTreesClassifier(n_estimators=100, n_jobs=-1,
        #                              random_state=random_state)
        }

#predictions on the validation and test sets
p_valid = []
p_test = []

print('Performance of individual classifiers (1st layer) on X_test')
print('------------------------------------------------------------')

instances_num = [1000, 2000, 5000, 8000, 10000, 20000, 50000, len(X)]

error_rate_for_clfs = {}
for nm, clf in clfs.iteritems():
    error_rate_for_clfs[str(nm)] = []

print error_rate_for_clfs

for i in range(0,len(instances_num),1):
    size = instances_num[i]
    X_train = X[0:size]
    y_train = y[0:size]
    print 'current size: ', len(X_train), len(y_train)

    for nm, clf in clfs.iteritems():
        print "start: " + str(nm)
        #First run. Training on (X_train, y_train) and predicting on X_valid.
        # clf.fit(X_train, y_train)
        # yv = clf.predict_proba(X_valid)
        # p_valid.append(yv)

        #Second run. Training on (X, y) and predicting on X_test.
        clf.fit(X_train, y_train)
        yt = clf.predict(X_test)
        yt_all = clf.predict_proba(X_test)
        p_test.append(yt_all)

        #Printing out the performance of the classifier
        error_rate = 1-accuracy_score(y_test, yt)
        error_rate_for_clfs[str(nm)].append(error_rate)
        print('{:10s} {:2s} {:1.7f}'.format('%s: ' %(nm), 'error rate  =>', error_rate))
        # print('{:10s} {:2s} {:1.7f}'.format('%s: ' %(nm), 'log loss  =>', log_loss(y_test, yt_all)))
    print('')

print error_rate_for_clfs