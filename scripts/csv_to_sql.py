import os
import csv
import sys

def csv_to_sql(csv_file_path, sql_file_path, table_name):
    with open(csv_file_path, 'r', newline='') as csvfile:
        reader = csv.DictReader(csvfile, delimiter=';')
        fieldnames = reader.fieldnames
        base_sql = f'INSERT INTO {table_name} ({", ".join(fieldnames)}) VALUES '
        with open(sql_file_path, 'w') as sqlfile:
            for row in reader:
                values = ', '.join([f'"{value}"' if isinstance(value, str) else str(value) for value in row.values()])
                sql_statement = f'{base_sql}({values});\n'
                sqlfile.write(sql_statement)

def process_files_in_directory(directory_path):
    for filename in os.listdir(directory_path):
        if filename.endswith('.csv'):
            csv_file_path = os.path.join(directory_path, filename)
            sql_file_path = os.path.join(directory_path, f'{os.path.splitext(filename)[0]}.sql')
            # Extract table name by splitting the filename and taking the last part before the extension
            table_name = os.path.splitext(filename)[0].split('-')[-1]
            csv_to_sql(csv_file_path, sql_file_path, table_name)
            print(f'Processed {csv_file_path} -> {sql_file_path}')

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python3 csv_to_sql.py <directory_path>")
        sys.exit(1)
    directory_path = sys.argv[1]
    process_files_in_directory(directory_path)
