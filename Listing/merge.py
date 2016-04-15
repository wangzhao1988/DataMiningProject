import csv
import os
import random

csvfile = open("./merge.csv", 'wb')
cwriter = csv.writer(csvfile)
initial = True
headers = []
for root, dirs, files in os.walk("./data"):
    for f in files:
        file_open = open(root + "/" + f, 'r')
        i = 0
        header_mapping = {}
        reader = csv.reader(file_open)
        for row in reader:
            if i==0 and initial:
                headers = list(row)
                headers.remove("description")
                headers.remove("amenities")
                headers.remove("square_feet")
                headers.remove("host_verifications")
                headers.remove("host_about")
                headers.remove("notes")
                headers.remove("summary")
                headers.remove("space")
                headers.remove("neighborhood_overview")
                headers.remove("transit")
                for j in range(len(row)):
                    if row[j] in headers:
                        header_mapping[row[j]] = j
                initial = False
                cwriter.writerow(headers)
            elif i == 0:
                for j in range(len(row)):
                    if row[j] in headers:
                        header_mapping[row[j]] = j
            else:
                content = []
                for item in headers:
                    if item in header_mapping and row[header_mapping[item]]:
                        cell = row[header_mapping[item]]
                        cell = cell.replace("\n", " ")
                        cell = cell.replace("\r", " ")
                        content.append(cell)
                    else:
                        content.append(" ")
                cwriter.writerow(content)
            i += 1

        file_open.close()

csvfile.close()