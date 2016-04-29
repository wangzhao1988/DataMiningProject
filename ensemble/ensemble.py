import numpy as  np
import pandas as pd
from sklearn.metrics import log_loss
from sklearn.metrics import mean_absolute_error
from sklearn.metrics import accuracy_score
from sklearn.base import BaseEstimator
from scipy.optimize import minimize
from sklearn.preprocessing import LabelEncoder
from sklearn import tree
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier, ExtraTreesClassifier, AdaBoostClassifier
from sklearn.svm import SVC
from sklearn.neighbors import KNeighborsClassifier
from sklearn.calibration import CalibratedClassifierCV
from sklearn.cross_validation import train_test_split

def objf_ens_optA(w, Xs, y, n_class=11):

    w = np.abs(w)
    sol = np.zeros(Xs[0].shape)
    for i in range(len(w)):
        sol += Xs[i] * w[i]
    #Using log-loss as objective function (different objective functions can be used here).
    score = log_loss(y, sol)
    # label_pred = []
    # for row in sol:
    #     label_pred.append(np.argmax(row))
    # score = 1-accuracy_score(y, label_pred)
    return score


class EN_optA(BaseEstimator):

    def __init__(self, n_class=11):
        super(EN_optA, self).__init__()
        self.n_class = n_class

    def fit(self, X, y):

        Xs = np.hsplit(X, X.shape[1]/self.n_class)
        #Initial solution has equal weight for all individual predictions.
        x0 = np.ones(len(Xs)) / float(len(Xs))
        #Weights must be bounded in [0, 1]
        bounds = [(0,1)]*len(x0)
        #All weights must sum to 1
        cons = ({'type':'eq','fun':lambda w: 1-sum(w)})
        #Calling the solver
        res = minimize(objf_ens_optA, x0, args=(Xs, y, self.n_class),
                       method='SLSQP',
                       bounds=bounds,
                       constraints=cons
                       )
        self.w = res.x
        return self

    def predict_proba(self, X):

        Xs = np.hsplit(X, X.shape[1]/self.n_class)
        y_pred = np.zeros(Xs[0].shape)
        for i in range(len(self.w)):
            y_pred += Xs[i] * self.w[i]
        return y_pred

    def predict(self, X):
        Xs = np.hsplit(X, X.shape[1]/self.n_class)
        y_pred = np.zeros(Xs[0].shape)
        for i in range(len(self.w)):
            y_pred += Xs[i] * self.w[i]

        label_pred = []
        for row in y_pred:
            label_pred.append(np.argmax(row))
        return label_pred

def objf_ens_optB(w, Xs, y, n_class=11):
    w_range = np.arange(len(w))%n_class
    for i in range(n_class):
        w[w_range==i] = w[w_range==i] / np.sum(w[w_range==i])

    sol = np.zeros(Xs[0].shape)
    for i in range(len(w)):
        sol[:, i % n_class] += Xs[int(i / n_class)][:, i % n_class] * w[i]

    #Using log-loss as objective function (different objective functions can be used here).
    score = log_loss(y, sol)
    # label_pred = []
    # for row in sol:
    #     label_pred.append(np.argmax(row))
    # score = 1-accuracy_score(y, label_pred)
    return score


class EN_optB(BaseEstimator):
    def __init__(self, n_class=11):
        super(EN_optB, self).__init__()
        self.n_class = n_class

    def fit(self, X, y):
        Xs = np.hsplit(X, X.shape[1]/self.n_class)
        #Initial solution has equal weight for all individual predictions.
        x0 = np.ones(self.n_class * len(Xs)) / float(len(Xs))
        #Weights must be bounded in [0, 1]
        bounds = [(0,1)]*len(x0)
        #Calling the solver (constraints are directly defined in the objective
        #function)
        res = minimize(objf_ens_optB, x0, args=(Xs, y, self.n_class),
                       method='L-BFGS-B',
                       bounds=bounds,
                       )
        self.w = res.x
        return self

    def predict_proba(self, X):
        Xs = np.hsplit(X, X.shape[1]/self.n_class)
        y_pred = np.zeros(Xs[0].shape)
        for i in range(len(self.w)):
            y_pred[:, i % self.n_class] += \
                   Xs[int(i / self.n_class)][:, i % self.n_class] * self.w[i]
        return y_pred

    def predict(self, X):
        Xs = np.hsplit(X, X.shape[1]/self.n_class)
        y_pred = np.zeros(Xs[0].shape)
        for i in range(len(self.w)):
            y_pred[:, i % self.n_class] += \
                   Xs[int(i / self.n_class)][:, i % self.n_class] * self.w[i]

        label_pred = []
        for row in y_pred:
            label_pred.append(np.argmax(row))
        return label_pred


#fixing random state
random_state=1
n_classes = 11

df_train = pd.read_csv('./preprocess.csv')
labels = df_train['destination'].values
df_train = df_train.drop(['destination'], axis=1)
le = LabelEncoder()
labels = le.fit_transform(labels)
cat_field = ['booking2CreateLag', 'booking2ActiveLag', 'gender', 'signupMethod', 'language', 'channel',
             'provider', 'tracked', 'signupApp', 'deviceType', 'browser']
for f in cat_field:
    df_train[f] = le.fit_transform(df_train[f])

#Spliting data into train and test sets.
X, X_test, y, y_test = train_test_split(df_train, labels, test_size=0.2,
                                        random_state=random_state)

#Spliting train data into training and validation sets.
X_train, X_valid, y_train, y_valid = train_test_split(X, y, test_size=0.2,
                                                      random_state=random_state)
# print(y_test)
print('Data shape:')
print('X_train: %s, X_valid: %s, X_test: %s \n' %(X_train.shape, X_valid.shape,
                                                  X_test.shape))

#Defining the classifiers
clfs = {
        'LR'  : LogisticRegression(random_state=random_state),
        # 'SVM' : SVC(probability=True, random_state=random_state),
        'RF'  : RandomForestClassifier(n_estimators=100, n_jobs=-1,
                                       random_state=random_state),
        'GBM' : GradientBoostingClassifier(n_estimators=50,
                                           random_state=random_state),
        'DT'  : tree.DecisionTreeClassifier(criterion="entropy"),
        # 'ADA' : AdaBoostClassifier(tree.DecisionTreeClassifier(criterion="entropy"), n_estimators=50,
        #                            random_state=random_state),
        'ETC' : ExtraTreesClassifier(n_estimators=100, n_jobs=-1,
                                     random_state=random_state),
        'KNN' : KNeighborsClassifier(n_neighbors=50)}

#predictions on the validation and test sets
p_valid = []
p_test = []

print('Performance of individual classifiers (1st layer) on X_test')
print('------------------------------------------------------------')

for nm, clf in clfs.iteritems():
    print "start: " + str(nm)
    #First run. Training on (X_train, y_train) and predicting on X_valid.
    clf.fit(X_train, y_train)
    yv = clf.predict_proba(X_valid)
    p_valid.append(yv)

    #Second run. Training on (X, y) and predicting on X_test.
    clf.fit(X, y)
    yt = clf.predict(X_test)
    yt_all = clf.predict_proba(X_test)
    p_test.append(yt_all)

    #Printing out the performance of the classifier
    print('{:10s} {:2s} {:1.7f}'.format('%s: ' %(nm), 'error rate  =>', 1-accuracy_score(y_test, yt)))
    print('{:10s} {:2s} {:1.7f}'.format('%s: ' %(nm), 'log loss  =>', log_loss(y_test, yt_all)))
print('')

print('Performance of optimization based ensemblers (2nd layer) on X_test')
print('------------------------------------------------------------')

#Creating the data for the 2nd layer.
XV = np.hstack(p_valid)
XT = np.hstack(p_test)

#EN_optA
enA = EN_optA(n_classes)
enA.fit(XV, y_valid)
w_enA = enA.w
y_enA = enA.predict(XT)
y_enA_all = enA.predict_proba(XT)
print('{:20s} {:2s} {:1.7f}'.format('EN_optA:', 'error rate  =>', 1-accuracy_score(y_test, y_enA)))
print('{:20s} {:2s} {:1.7f}'.format('EN_optA:', 'log loss  =>', log_loss(y_test, y_enA_all)))

# Calibrated version of EN_optA
cc_optA = CalibratedClassifierCV(enA, method='isotonic')
cc_optA.fit(XV, y_valid)
y_ccA = cc_optA.predict(XT)
y_ccA_all = cc_optA.predict_proba(XT)
print('{:20s} {:2s} {:1.7f}'.format('Calibrated_EN_optA:', 'error rate  =>', 1-accuracy_score(y_test, y_ccA)))
print('{:20s} {:2s} {:1.7f}'.format('Calibrated_EN_optA:', 'log loss  =>', log_loss(y_test, y_ccA_all)))

#EN_optB
enB = EN_optB(n_classes)
enB.fit(XV, y_valid)
w_enB = enB.w
y_enB = enB.predict(XT)
y_enB_all = enB.predict_proba(XT)
print('{:20s} {:2s} {:1.7f}'.format('EN_optB:', 'error rate  =>', 1-accuracy_score(y_test, y_enB)))
print('{:20s} {:2s} {:1.7f}'.format('EN_optB:', 'log loss  =>', log_loss(y_test, y_enB_all)))

#Calibrated version of EN_optB
cc_optB = CalibratedClassifierCV(enB, method='isotonic')
cc_optB.fit(XV, y_valid)
y_ccB = cc_optB.predict(XT)
y_ccB_all = cc_optB.predict_proba(XT)
print('{:20s} {:2s} {:1.7f}'.format('Calibrated_EN_optB:', 'error rate  =>', 1-accuracy_score(y_test, y_ccB)))
print('{:20s} {:2s} {:1.7f}'.format('Calibrated_EN_optB:', 'log loss  =>', log_loss(y_test, y_ccB_all)))

w_final = [2.0/13, 4.0/13, 2.0/13, 5.0/13]
XX = np.hstack([y_enA_all, y_ccA_all, y_enB_all, y_ccB_all])
en3 = EN_optA(n_classes)
en3.fit(XX, y_valid)
y_final_all = en3.predict_proba(XX)
# y_final_all = y_enA_all*w_final[0] + y_ccA_all*w_final[1] + y_enB_all*w_final[2] + y_ccB_all*w_final[3]
y_final = []
for row in y_final_all:
    y_final.append(np.argmax(row))

print('{:20s} {:2s} {:1.7f}'.format('3rd_layer:', 'error rate  =>', 1-accuracy_score(y_test, y_final)))
print('{:20s} {:2s} {:1.7f}'.format('3rd_layer:', 'logloss  =>', log_loss(y_test, y_final_all)))
