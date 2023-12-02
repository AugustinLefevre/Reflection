import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class StartTheExerciceHere {

    private static List<String> allClassNames = new ArrayList<>();

    private static final String CLASS_EXTENTION = ".class";

    /**
     *Here we have to a main method to run our own class
     */
    public static void main(String ... args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        executeMainMethod();
    }

    /**
     *This method search all classes with a main method and run the methods fund
     */
    private static void executeMainMethod() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources("");
        File rootDir = new File(resources.nextElement().getFile());
        String projectName = rootDir.getName();

        findAllClassNames(rootDir, projectName);

        List<String> mainClassNames = findClassWithMethodName("main");

        mainClassNames.stream().forEach(System.out::println);

        for (String mainClassName : mainClassNames){
            Method methodMain = Class.forName(mainClassName).getDeclaredMethod("main", String[].class);
            methodMain.setAccessible(true);
            methodMain.invoke(null, (Object) null);
        }
    }

    /**
     * This method looks for all classes with a given method name
     * @param methodName the given method name to search
     * @return a list of class name
     */
    private static List<String> findClassWithMethodName(String methodName) {
        List<String> results = new ArrayList<>();
        for (String className : allClassNames){
            try {
                Class.forName(className).getMethod(methodName, String[].class);
                if(!className.equals(StartTheExerciceHere.class.getName())){
                    results.add(className);
                }
            } catch (NoSuchMethodException e) {
                System.err.println("No " + methodName + " method found in class " + className);
            }catch (ClassNotFoundException e){
                System.err.println("Class " + className + " not found");
            }
        }
        return results;
    }

    /**
     * This method return all classes names in the project
     * @param file the file used to do a recursive iteration. Check if it's a directory or if it's a class file.
     * @param projectName The project name used to guess the class package name
     */
    private static void findAllClassNames(File file, String projectName) throws ClassNotFoundException {
        if(file.isDirectory()){
            List<File> childs = Arrays.asList(file.listFiles());
            for (File child : childs){
                findAllClassNames(child, projectName);
            }
        }else {
            if(file.getName().contains(CLASS_EXTENTION)){
                StringBuilder splitPattern = new StringBuilder(projectName);
                splitPattern.append('\\');
                splitPattern.append(FileSystems.getDefault().getSeparator());
                String[] pathChunks = file.getPath().split(splitPattern.toString());
                for (String pathChunk : pathChunks){
                    if(pathChunk.contains(CLASS_EXTENTION)){
                        pathChunk = pathChunk.replaceAll("\\\\", ".");
                        allClassNames.add(pathChunk.replace(CLASS_EXTENTION, ""));
                    }
                }
            }
        }
    }
}
