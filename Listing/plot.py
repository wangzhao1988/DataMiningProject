import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import csv
import re

# plt.interactive(False)
# chunksize = 1000
merge_file = open('./merge.csv')
reader = csv.reader(merge_file)
i = 0
col = ['country', 'review_scores_rating']
data = {col[i]: [] for i in range(len(col))}
header_mapping = {}
for row in reader:
    if i==0:
        for j in range(len(row)):
            header_mapping[row[j]] = j
        i+=1
        continue
    for j in range(len(col)):
        data[col[j]].append(row[header_mapping[col[j]]])
    i += 1

# temp = map(lambda x: float(re.sub(r'\$*,*', '', x)), data['price'])
# data['price'] = list(temp)

df = pd.DataFrame(data)
df['review_scores_rating'] = pd.to_numeric(df['review_scores_rating'], errors='coerce').dropna()
print df['review_scores_rating']

my_plot = df.groupby(['country'])['review_scores_rating'].mean().plot(kind='bar')
my_plot.set_ylabel('Review Score')
plt.title("Country vs. Average review score")

plt.show()

# for row in reader:
#     col = row
#     break
#
# i = 0
# for chunk in pd.read_csv('./merge.csv', chunksize =chunksize, na_values=['NA'], header=0):
#     i += 1
#     if i == 50:
#         print
#         df = pd.DataFrame(chunk, columns=col)
#         print df['price']