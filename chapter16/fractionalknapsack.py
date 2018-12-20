# Fractional Knapsack Program
# Greedy Algorithm
# Runs in O(nlgn)...this includes the time to sort in decreasing order as well
# If already sorted and sorting part of program is removed then works in linear time: O(n)
# Function takes in list of format -> [[item mass1, item value1]...and so on]


def solveknapsack(tup, capacity):
    for i in range(len(tup)):
        value_per_mass = tup[i][1] / tup[i][0]
        tup[i].append(value_per_mass)
    tup = sorted(tup, key = lambda item:item[2])
    i = len(tup) - 1
    total_mass = 0
    total_value = 0
    while(i >= 0 and total_mass <= capacity):
        if(total_mass + tup[i][0] <= capacity):
            total_value += tup[i][1]
            total_mass += tup[i][0]
            print("Item Used: ", i + 1)
        else:
            mass_available = capacity - total_mass
            total_value += tup[i][2] * mass_available
            total_mass += tup[i][0]
            print("Item Used: ", i + 1)
        i -= 1    
    return total_value             
        

if __name__ == "__main__":
    capacity = int(input("Enter knapsack weight:\n"))
    x = [float(x) for x in input("Enter Mass followed by Total Value:\n").split()]
    i = 0
    tuplelized_List = []
    while(i < len(x)):
        tuplelized_List.append(x[i:i + 2])
        i += 2
    
    total_value = solveknapsack(tuplelized_List, capacity)
    print("Total Value:", total_value)
             