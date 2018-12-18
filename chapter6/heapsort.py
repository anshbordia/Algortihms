# Heap Sort Program
# In Place Sorting Algorithm
# Worst Case Running Time: O(nlgn)


def leftchild(i):
    return (int(2 * i + 1))

def rightchild(i):
    return (int(2 * i + 2))

def parent(i):
    return (int((i - 1) / 2))

def maxheapify(a, i, a_heapsize):
    l = leftchild(i)
    r = rightchild(i)
    if(l < a_heapsize and a[l] > a[i]):
        largest = l
    else:
        largest = i
    if(r < a_heapsize and a[r] > a[largest]):
        largest = r    
    if largest != i:
        a[i],a[largest] = a[largest], a[i]
        maxheapify(a, largest, a_heapsize)

def buildmaxheap(a, a_heapsize):
    for i in range(int((len(a) / 2 - 1)), -1, -1):
        maxheapify(a, i, a_heapsize)
        
def heapsort(a, a_heapsize):
    buildmaxheap(a, a_heapsize)
    for i in range((len(a) - 1), 0, -1):
        a[0], a[i] = a[i], a[0]
        a_heapsize -= 1
        maxheapify(a, 0, a_heapsize)
    return a

print("Please enter an array: ")
a = [int(x) for x in input().split()]
a_heapsize = len(a)
heapsort(a, a_heapsize)
print("Sorted: ",a)