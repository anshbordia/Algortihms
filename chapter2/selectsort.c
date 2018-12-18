#include <stdio.h>
#include <stdlib.h>

void selectsort(int numberlist[], int count, int choice);
int findmin(int numberlist[], int count, int cur_pos);

int main() {
	int *numberlist, n, i, choice;
	printf("Enter the number of elements in your list: ");
	scanf("%d", &n);
	numberlist = (int *)malloc(n * sizeof(int));
	printf("Enter 1 to sort in ascending/2 for descending\n");
	scanf("%d", &choice);
	printf("\nEnter the numbers: \n");
	for(i = 0; i < n; i++)
		scanf("%d", &numberlist[i]);
	selectsort(numberlist, n, choice);
	return 0;
}

void selectsort(int a[], int n, int c) {
	int i, j, min_index, temp;
	for(i = 0; i < n - 1; i++){
		min_index = findmin(a, n, i);
		temp = a[i];
		a[i] = a[min_index];
		a[min_index] = temp;
	}
	printf("\n");
	if(c == 1) {
		for(i = 0; i < n; i++)
			printf("%d ", a[i]);
	}
	else {
		for(i = n - 1; i >= 0; i--)
			printf("%d ", a[i]);
	}	
	return;
}


int findmin(int a[], int n, int cur_pos) {
	int i, min = a[cur_pos], min_index = cur_pos;
	for(i = cur_pos + 1; i < n; i++) {
		if(a[i] < min) {
			min = a[i];
			min_index = i;
		}	
	}
	return min_index;
}