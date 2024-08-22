#include <stdio.h>
#include <stdlib.h>
#include <string.h>

FILE *fili, *filo;

int main(int argv, char** argc)
{
	long a, b;
	if (argv == 2)
	{
	char fi[1000];
	strcpy(fi, argc[argv-1]);
	strcat(fi, ".txt");
	char fo[1000];
	strcpy(fo, argc[argv-1]);
	strcat(fo, ".gml");
	//printf("%s\n", fi);
	//printf("%s\n", fo);	
	fili = fopen(fi, "r");
	long largest = 0;
	while(~fscanf(fili, "%ld", &a))
	{
		if(largest < a)		largest = a;
	}
	//printf("%ld",largest);
	fclose(fili);
	
	filo = fopen(fo, "w");
	fprintf(filo, "Creator \"Sepide Banihashemi - Source file: %s\"\n", fi);
	fprintf(filo, "graph\n[\n");
	
	long i;
	for(i=0;i<largest;i++)
	{
		fprintf(filo, "  node\n  [\n    id %ld\n  ]\n", (i+1));
	}
	
	fili = fopen(fi, "r");
	while(~fscanf(fili, "%ld %ld", &a, &b))
	{
		fprintf(filo, "  edge\n  [\n    source %ld\n    target %ld\n  ]\n", a, b);
	}
	fprintf(filo, "]");
	fclose(fili);
	fclose(filo);
	printf("\nConversion complete!!!\nCheck file - %s\n\n", fo);
	}
	else
	{
		printf("\nTXT2GML Converter - Sepide Banihashemi\n\nWould convert the .txt data file to .gml data file.\nDo not give '.txt' extension while entering file name.\nMissing : <filename> as parameter\n\n");
		exit(0);
	}
return 0;
}
