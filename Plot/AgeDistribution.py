import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt

# Set figure aesthetics
sns.set_style("white", {'ytick.major.size': 10.0})
sns.set_context("poster", font_scale=1.1)

users = pd.read_csv("/Users/hengxu/Documents/NEU/Classes/DataMining/DataMiningProject/Data/Users.csv")
users.age.describe()
# print(sum(users.age > 122))
# print(sum(users.age < 18))

sns.distplot(users.age.dropna(), color='#b1d1fc')
plt.xlabel('Age')
sns.despine()


plt.show()