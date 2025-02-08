import sys

def convert_to_gml(filename):
    input_file = filename + ".txt"
    output_file = filename + ".gml"

    # First pass: find largest node id (checking both source and target)
    largest = 0
    with open(input_file, "r") as inFile:
        for line in inFile:
            nums = [int(x) for x in line.strip().split()]
            if len(nums) >= 2:
                largest = max(largest, nums[0], nums[1])  # Check both source and target

    # Write GML file
    with open(output_file, "w") as outFile:
        # Write header with the exact source file path
        outFile.write(f'Creator "Sepide Banihashemi - Source file: {input_file}"\n')
        outFile.write('graph\n[\n')

        # Write all nodes up to the largest ID
        for i in range(largest):
            outFile.write(f'  node\n  [\n    id {i+1}\n  ]\n')

        # Write edges
        with open(input_file, "r") as inFile:
            for line in inFile:
                nums = [int(x) for x in line.strip().split()]
                if len(nums) >= 2:
                    a, b = nums[0], nums[1]
                    outFile.write(f'  edge\n  [\n    source {a}\n    target {b}\n  ]\n')

        # Close graph
        outFile.write(']')

    print(f"\nFile Converted as: {output_file}\n")

def main():
    if len(sys.argv) == 2:
        convert_to_gml(sys.argv[1])
    else:
        print("\nTXT2GML Converter - Sepide Banihashemi, Python Version - Daniel Ibanescu")
        print("\nConverts TXT to GML format")
        print("Enter only file name, without extension")
        print("Missing : <filename> as parameter\n")
        sys.exit(0)

if __name__ == "__main__":
    main()