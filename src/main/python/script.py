import collections
import re
import os
import sys
import glob
import datetime
import pprint
import pandas as pd
import numpy as np
from scipy import stats


def print_stats(scores, file):
    values = []
    for i, s in scores.iteritems():
        try:
            s = re.sub(r':.*?=', ":", str(s))
        except TypeError:
            raise TypeError("S: " + str(s))
        try:
            d = eval(s)
            values += d.values()
        except:
            print("evaluation of " + file + " at row " + str(i) + ": " + str(s) + " failed because of " + str(sys.exc_info()[0]))
    arr = np.array(values)
    d = stats.describe(arr)
    s = "min-max: %s, mean: %f, var: %f"
    return s % (str(d[1]), d[2], d[3])


def top_k_scores_stats():
    os.chdir("..\\..\\..\\target\\classes\\ws\\logs")
    print(os.getcwd(), os.listdir(os.getcwd()))
    now = datetime.datetime.now().strftime('%Y_%m_%d_%H_%M_%S')
    file_name = "stats_scores__" + now + ".txt"

    simple_weight_logs = glob.glob("./simple_weight_*.txt")
    print("simple scores:", simple_weight_logs)
    with open(file_name, "a+") as f:
        f.write("\n *** SIMPLE SCORES *** \n")
        for swl in simple_weight_logs:
            df = pd.read_csv(swl, sep="\t", names=["year", "scores"])
            f.write("\n" + swl + "\t\t--> " + print_stats(df["scores"], swl))

    page_rank_logs = glob.glob("./page_rank_*.txt")
    print("page rank scores:", page_rank_logs)
    with open(file_name, "a+") as f:
        f.write("\n\n\n *** PAGE RANK SCORES *** \n")
        for prl in page_rank_logs:
            df = pd.read_csv(prl, sep="\t", names=["year", "scores"])
            f.write("\n" + prl + "\t\t--> " + print_stats(df["scores"], prl))

    scoring_logs = glob.glob("./scoring_*.txt")
    print("scoring:", scoring_logs)
    with open(file_name, "a+") as f:
        f.write("\n\n\n *** COMBINED SCORING *** \n")
        for sl in scoring_logs:
            df = pd.read_csv(sl, sep="\t", names=["year", "scores", "normalized_scores", "weights", "combined"])
            f.write("\n\n" + sl + ":")
            f.write("\ncentrality scores:\t\t" + print_stats(df["scores"], sl))
            f.write("\nnormalized scores:\t\t" + print_stats(df["normalized_scores"], sl))
            if not df["weights"].isnull().all():
                f.write("\nweight scores:\t\t\t" + print_stats(df["weights"], sl))
            if not df["combined"].isnull().all():
                f.write("\ncombined scores:\t\t" + print_stats(df["combined"], sl))


def independent_cascade_stats():
    os.chdir("..\\..\\..\\target\\classes\\ws\\logs")

    s = "{4077=[4077], 8324=[8324], 1264=[1264, 4942, 4932, 4934, 4946, 4928, 4938, 4077, 4930, 4940, 1263, 1263, 4930, 4944, 1263, 4944, 4077, 4930, 1263, 4930, 4928, 1263, 4930, 4159, 8326, 8324, 8322, 8320, 8318, 8318, 8316, 8320, 8318, 8316, 8324, 8322, 8320, 8318, 4075], 1263=[1263], 4938=[4938], 102=[102, 3526], 4928=[4928], 642=[642, 609], 610=[610, 2504, 642, 11224], 4940=[4940], 4159=[4159], 4930=[4930], 8316=[8316], 4942=[4942], 638=[638, 2436, 7650, 7647, 7647], 4932=[4932], 85=[85, 7208, 103, 638, 102, 3528, 106, 1410, 1406, 1643, 2578, 4650, 4644, 4640, 2925, 2925, 108, 3528, 1548, 1560, 1546, 1554, 1556, 1552, 1558, 1552, 1545, 86], 4944=[4944], 4934=[4934], 4946=[4946]}"
    s = s.replace("=", ":")
    d = eval(s)
    l = []
    for x in d:
        l += d[x]
    print(len(l), len(set(l)))


def create_dirs():
    os.chdir("..\\..\\..\\target\\classes\\ws\\plots")

    metrics = ["clu", "bet", "clo", "alp", "pag"]
    types = ["_simple", "_weighted", "_pr", "_prw", "_prw_mul", "_prw_unb", "_prw_unb_mul"]
    for m in metrics:
        for t in types:
            directory = m+t
            if not os.path.exists(directory):
                os.makedirs(directory)
                print("create " + directory)
            else:
                print(directory + " already exists")


def check_infected_count(year, numSeeds):
    if os.path.basename(os.getcwd()) != "logs":
        os.chdir("..\\..\\..\\target\\classes\\ws\\logs")
    results_logs = glob.glob("./ic_results__*.txt")
    results_logs.sort()
    results_logs = results_logs[-30:]
    print(results_logs)
    counter = collections.defaultdict(collections.Counter)
    for rl in results_logs:
        df = pd.read_csv(rl, sep="\t")
        mymap = eval(df.loc[df["year"] == year].loc[df["numSeeds"] == numSeeds, "infectedNodes"].values[0])
        for k, v in mymap.items():
            counter[k] += collections.Counter(v)
    pprint.pprint(counter)


if __name__ == '__main__':
    # top_k_scores_stats()
    # create_dirs()
    check_infected_count(2001, 5)
    check_infected_count(2018, 10)
