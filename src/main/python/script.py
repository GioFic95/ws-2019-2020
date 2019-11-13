import re
import os
import sys
import pandas as pd
import numpy as np
from scipy import stats


os.chdir("..\\target\classes\ws\logs")


def print_stats(scores):
    values = []
    for i,s in scores.iteritems():
        try:
            s = re.sub(r':.*?=', ":", str(s))
        except TypeError:
            raise TypeError("S: " + str(s))
        try:
            d = eval(s)
            values += d.values()
            print(stats.describe(np.array(values)))
        except:
            print("evaluation of " + str(s) + " failed because of " + str(sys.exc_info()[0]))
    
    print("\n\n\n")
    # print(values)
    arr = np.array(values)
    print(stats.describe(arr))


def top_k_scores_stats():
    # closeness centrality
    print("*** closeness centrality ***")
    
    print("simple scores:")
    df = pd.read_csv("simple_weight_ccprwunb.txt", sep="\t", names=["year", "scores"])
    simple_scores = df["scores"]
    print_stats(simple_scores)
    # DescribeResult(nobs=17424, minmax=(0.005089058524173028, 1.0), mean=0.1786927011522485, variance=0.055404740904383486, skewness=2.174640270863546, kurtosis=4.18094252335371)

    print("\npage rank scores:")
    df = pd.read_csv("page_rank_ccprwunb.txt", sep="\t", names=["year", "scores"])
    pr_scores = df["scores"]
    print_stats(pr_scores)
    # DescribeResult(nobs=8712, minmax=(0.0, 1.0), mean=0.16408812878786772, variance=0.05497926632319948, skewness=2.217015566565808, kurtosis=4.371053728450705)

    df = pd.read_csv("scoring_ccprwunb.txt", sep="\t", names=["year", "scores", "weights", "combined"])
    print("\ncentrality scores:")
    centrality_scores = df["scores"]
    print_stats(centrality_scores)
    # DescribeResult(nobs=8712, minmax=(0.00865176640230715, 1.0), mean=0.4064756171031155, variance=0.08274448107227023, skewness=-3.2355742752306665e-06, kurtosis=-1.2551536789194064)
    
    print("\nweight scores:")
    weight_scores = df["weights"]
    print_stats(weight_scores)
    # DescribeResult(nobs=8712, minmax=(0.0015624999999999999, 1.0), mean=0.1684695004971819, variance=0.05360682063529081, skewness=2.2406955298485816, kurtosis=4.490444852087746)
    
    print("\ncombined scores:")
    combined_scores = df["combined"]
    print_stats(combined_scores)
    # DescribeResult(nobs=8712, minmax=(0.0015624999999999999, 1.0), mean=0.1684695004971819, variance=0.05360682063529081, skewness=2.2406955298485816, kurtosis=4.490444852087746)


    # clustering coefficient
    print("\n\n\n*** clustering coefficient ***")
    
    print("simple scores:")
    df = pd.read_csv("simple_weight_ccoeffprwunb.txt", sep="\t", names=["year", "scores"])
    simple_scores = df["scores"]
    print_stats(simple_scores)
    # DescribeResult(nobs=8712, minmax=(0.005089058524173028, 1.0), mean=0.1786927011522485, variance=0.05540792106400376, skewness=2.174640270863546, kurtosis=4.18094252335371)

    print("\npage rank scores:")
    df = pd.read_csv("page_rank_ccoeffprwunb.txt", sep="\t", names=["year", "scores"])
    pr_scores = df["scores"]
    print_stats(pr_scores)
    # DescribeResult(nobs=8712, minmax=(0.0, 1.0), mean=0.16408812878786772, variance=0.05497926632319948, skewness=2.217015566565808, kurtosis=4.371053728450705)

    df = pd.read_csv("scoring_ccoeffprwunb.txt", sep="\t", names=["year", "scores", "weights", "combined"])
    print("\ncentrality scores:")
    centrality_scores = df["scores"]
    print_stats(centrality_scores)
    # DescribeResult(nobs=8712, minmax=(0.0, 1.0), mean=0.6908857757809277, variance=0.17791444903162126, skewness=-0.8232547782413364, kurtosis=-1.127946675919553)
    
    print("\nweight scores:")
    weight_scores = df["weights"]
    print_stats(weight_scores)
    # DescribeResult(nobs=8712, minmax=(0.0015624999999999999, 1.0), mean=0.1684695004971819, variance=0.05360682063529081, skewness=2.2406955298485816, kurtosis=4.490444852087746)
    
    print("\ncombined scores:")
    combined_scores = df["combined"]
    print_stats(combined_scores)
    # DescribeResult(nobs=8712, minmax=(0.00046874999999999993, 1.0), mean=0.534160893195804, variance=0.10035221380698361, skewness=-0.771546242710575, kurtosis=-0.9588139512819436)

    
    # clustering coefficient multiplication
    print("\n\n\n*** clustering coefficient mult ***")

    df = pd.read_csv("scoring_ccoeffprwmul.txt", sep="\t", names=["year", "scores", "weights", "combined"])
    print("\ncentrality scores:")
    centrality_scores = df["scores"]
    print_stats(centrality_scores)
    # DescribeResult(nobs=8712, minmax=(0.0, 1.0), mean=0.6908857757809277, variance=0.17791444903162126, skewness=-0.8232547782413364, kurtosis=-1.127946675919553)
    
    print("\nweight scores:")
    weight_scores = df["weights"]
    print_stats(weight_scores)
    # DescribeResult(nobs=8712, minmax=(0.0026041666666666665, 1.0), mean=0.17139041497005808, variance=0.05340663880056598, skewness=2.233701831184674, kurtosis=4.4791550855195155)
    
    print("\ncombined scores:")
    combined_scores = df["combined"]
    print_stats(combined_scores)
    # DescribeResult(nobs=8712, minmax=(0.0, 1.0), mean=0.257010306941628, variance=0.07880619664368106, skewness=1.3089020480386901, kurtosis=0.8932524464261515)

def independent_cascade_stats():
    s = "{4077=[4077], 8324=[8324], 1264=[1264, 4942, 4932, 4934, 4946, 4928, 4938, 4077, 4930, 4940, 1263, 1263, 4930, 4944, 1263, 4944, 4077, 4930, 1263, 4930, 4928, 1263, 4930, 4159, 8326, 8324, 8322, 8320, 8318, 8318, 8316, 8320, 8318, 8316, 8324, 8322, 8320, 8318, 4075], 1263=[1263], 4938=[4938], 102=[102, 3526], 4928=[4928], 642=[642, 609], 610=[610, 2504, 642, 11224], 4940=[4940], 4159=[4159], 4930=[4930], 8316=[8316], 4942=[4942], 638=[638, 2436, 7650, 7647, 7647], 4932=[4932], 85=[85, 7208, 103, 638, 102, 3528, 106, 1410, 1406, 1643, 2578, 4650, 4644, 4640, 2925, 2925, 108, 3528, 1548, 1560, 1546, 1554, 1556, 1552, 1558, 1552, 1545, 86], 4944=[4944], 4934=[4934], 4946=[4946]}"
    s = s.replace("=", ":")
    d = eval(s)
    l = []
    for x in d:
        l += d[x]
    print(len(l), len(set(l)))


if __name__ == '__main__':
    top_k_scores_stats()
    
