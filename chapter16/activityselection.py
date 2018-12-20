# Activity Selection Program for activity scheduling
# Greedy Algorithm
# Done in 2 ways: 1)Recursion & 2)Iteration
# Activities are first sorted using a sorting algorithm of running time: O(nlgn)
# The greedy approach after sorting works in linear time: O(n)
# Input Type: List of Lists -> [[start1, end1], [start2, end2]...[startn, endn]] if using just function
# Eg [[1,5] [4, 8]....and so on]
# If using full program then input strictly in this format: s1 e1 s2 e2 s3 e3 and so on. 
# Note start time must be less than end time obviously

def itrscheduleactivity(t_list):
    activities = [1]
    k = 0
    for i in range(1, len(t_list)):
        if(t_list[i][0] >= t_list[k][1]):
            activities.append(i + 1)
            k = i
    
    return activities 


def recscheduleactivity(t_list, k):
    i = k + 1
    while(i < len(t_list) and t_list[i][0] < t_list[k][1]):
        i += 1    
    if(i < len(t_list)):
        print("Activity:", i + 1)
        return recscheduleactivity(t_list, i)
    else:
        return
if __name__ == "__main__":
    x = [float(x) for x in input("Please enter activity schedule in the format s1 e1 s2 e2....sn en: \n").split()]
    i = 0
    tuplelized_List = []
    while(i < len(x)):
        tuplelized_List.append(x[i:i + 2])
        i += 2
    tuplelized_List = sorted(tuplelized_List, key = lambda item:item[1])
    extra_tuplist = tuplelized_List.copy()
    activities = itrscheduleactivity(extra_tuplist)
    print("Using Iterative Method: ")
    for activity in activities:
        print("Activity:", activity)
    print("Using Recursive Method:")
    print("Activity:", 1)    
    recscheduleactivity(tuplelized_List, 0)
        