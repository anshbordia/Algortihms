// Quicksort Program Implementation
// In Place Sorting Algorithm
// Worst Case Running Time: O(n^2), but in most cases its expected run time is: (nlgn)
// Expected time running is faster compared to Heap Sort and Merge Sort (both of them also run in nlgn but quicksort's hidden constants are much smaller)
// Pivot is randomly chosen instead of continuously choosing last element


#include <stdio.h>
#include <stdlib.h>
#include <time.h>


int partition(int array[], int startindex, int endindex);
int rpartition(int array[], int startindex, int endindex);
void rquicksort(int array[], int startindex, int endindex);

int main() {
	int *numberlist, n, i;
	printf("Enter the number of elements in your list: ");
	scanf("%d", &n);
	numberlist = (int *)malloc(n * sizeof(int));
	printf("Enter the numbers: \n");
	for(i = 0; i < n; i++)
		scanf("%d", &numberlist[i]);
	srand(time(0));
	rquicksort(numberlist, 0, n - 1);
	for(i = 0; i < n; i++) {
		printf("%d ", numberlist[i]);
	}
	printf("\n");
	return 0;
}

int rpartition(int a[], int p, int r){
	int temp, i;
	i = (rand() % (r - p + 1)) + p;
	temp = a[r];
	a[r] = a[i];
	a[i] = temp;
	return (partition(a, p, r));
}

int partition(int a[], int p, int r) {
	int pivot, i, j, temp;
	pivot = a[r];
	i = p - 1;
	for(j = p; j < r; j++) {
		if(a[j] <= pivot) {
			i = i + 1;
			temp = a[j];
			a[j] = a[i];
			a[i] = temp;
		}
	}
	temp = a[i + 1];
	a[i + 1] = a[r]; 
	a[r] = temp;
	return (i + 1);
}

void rquicksort(int a[], int p, int r) {
	int q;
	if (p < r) {
		q = rpartition(a, p, r);
		rquicksort(a, p, q - 1);
		rquicksort(a, q + 1, r);
	}
	return;
}
