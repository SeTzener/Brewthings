import xml.etree.ElementTree as ET

file_path = "app/src/main/res/values/strings.xml"

tree = ET.parse(file_path)
root = tree.getroot()

# Extract and sort <string> elements alphabetically by name
strings = sorted(root.findall("string"), key=lambda x: x.get("name"))

# Remove old elements and insert sorted ones
for elem in root.findall("string"):
    root.remove(elem)
for string in strings:
    root.append(string)

# Save the sorted XML
tree.write(file_path, encoding="utf-8", xml_declaration=True)

print("Sorted strings.xml successfully!")
