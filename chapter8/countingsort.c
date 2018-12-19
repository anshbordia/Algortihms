#include <stdio.h>
#include <stdlib.h>


int countingsort(int inputarray[], int inputarray_size, int max);

int main() {
	int *numberlist, n, i, max = -32000;
	printf("Enter the number of elements in your list: ");
	scanf("%d", &n);
	numberlist = (int *)malloc(n * sizeof(int));
	printf("Enter the numbers: \n");
	for(i = 0; i < n; i++) {
		scanf("%d", &numberlist[i]);
		if(numberlist[i] > max) {
			max = numberlist[i];
		}
	}	
	countingsort(numberlist, n, max);
	return 0;
}

int countingsort(int a[], int n, int k) {
	int sorted[n], aux[k + 1], i, j;
	for(i = 0; i < k + 1; i++) {
		aux[i] = 0;
	}
	for(j = 0; j < n; j++) {
		aux[a[j]] = aux[a[j]] + 1;
	}
	/*printf("\nAux:");
	for(i = 0; i < k + 1; i++) {
		printf("%d ", aux[i]);
	}*/
	for(i = 1; i < k + 1; i++) {
		aux[i] = aux[i] + aux[i - 1];
	}
	
	/*printf("\nAuxsum:");
	for(i = 0; i < k + 1; i++) {
		printf("%d ", aux[i]);
	}*/
	for(j = n - 1; j >= 0; j--) {
		sorted[aux[a[j]] - 1] = a[j];
		aux[a[j]] = aux[a[j]] - 1;
	}
	printf("\n");
	for(i = 0; i < n; i++) {
		printf("%d ", sorted[i]);
	}
	printf("\n");
}
