import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class StartTheExerciceHere {

    private static List<String> allClassNames = new ArrayList<>();

    private static final String CLASS_EXTENTION = ".class";
    public static void main(String ... args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        executeMainMethod();
    }

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

    private static void findAllClassNames(File file, String projectName) {
        if(file.isDirectory()){
            List<File> childs = Arrays.asList(file.listFiles());
            for (File child : childs){
                findAllClassNames(child, projectName);
            }
        }else {
            if(file.getName().contains(CLASS_EXTENTION)){
                String test = FileSystems.getDefault().getSeparator();
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
