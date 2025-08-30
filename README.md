# MapCreatorForLibGDX

**MapCreatorForLibGDX** is a rather flexible tool for creating game entities in LibGDX using .tmx files. 

To get started, you need to make sure that you have imported the following: 
1) com.badlogicgames.ashley:ashley, 
2) com.badlogicgames.gdx:gdx-box2d,
3) com.badlogicgames.gdx:gdx

Also - please check your .tmx or .tsx files for the presence of fields where the "Class" field is present. It needs to be replaced with the "type" field, since due to the peculiarities of LibGDX, the "class" field is simply not parsed.
To replace all "class" with "type" at once, you can use this simple code:
```
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String filename = "";
        while (true){
            if (filename.equals(".tmx") || filename.equals(".tsx")) break;

            System.out.println("Please enter a file (.tmx/.tsx): ");
            filename = scanner.nextLine();
        }

        System.out.print("Please enter the path of file " + filename + ": ");
        String path = scanner.nextLine();

        if(!path.endsWith(filename)) {
            System.out.println("File is not " + filename + "!");
            return;
        }

        File tempFile = new File(path.replace(filename, "New") + filename);
        try (BufferedReader reader = new BufferedReader(new FileReader(path));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("class")) {
                    currentLine = currentLine.replace("class", "type");
                }

                writer.write(currentLine + "\n");
            }

        } catch (IOException e) {
            System.out.println("File not found!");
            throw new RuntimeException(e);
        }
    }
}
```


**Otherwise, the tool will not work!**

