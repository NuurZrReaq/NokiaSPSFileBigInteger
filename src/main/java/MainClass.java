import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MainClass {







    public static void main(String[] args) throws  Exception {

        if (args.length !=1) {
            throw new RuntimeException();
        } else {
            String path = args[0];

            ForkJoinPool pool = new ForkJoinPool(8);
            FileCount fileCount = new FileCount(Paths.get(path).toRealPath());
            //FileCount fileCount = new FileCount(Paths.get("C:\\Users\\NoorZ\\Documents\\dir").toRealPath());
            //FileCount fileCount = new FileCount(Paths.get("C:\\Users\\NoorZ\\Documents\\NokiaTaskFileConcurrency\\directory").toRealPath());
          //  Long current = System.currentTimeMillis();
            BigInteger [] bigIntegers = pool.invoke(fileCount);
            for(int i=0;i<bigIntegers.length;i++){
                System.out.print((char)(i+'a') + "\t"+bigIntegers[i]+'\n');
            }
            //fileCount.printLetterCount();
            //System.out.println();
           // Long after = System.currentTimeMillis();
            //System.out.println(after - current);
        }
    }




}




class FileCount extends RecursiveTask<BigInteger[]> {



    private final Path filePath;
    //private  static long [] letterCount = new long[26];


    public FileCount(Path filePath) {
        this.filePath = filePath;
    }




    public  BigInteger [] countLowerCase(File file)  {
        BigInteger  [] tempLetterCount = new BigInteger[26];
        init(tempLetterCount);
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream fileReader = new BufferedInputStream(fin);
            int c;
            while ((c=fileReader.read() )!= -1){

                if (Character.isLowerCase((char)c) && c>='a'&&c<='z') {

                    try {
                        tempLetterCount[(char)c-'a']=tempLetterCount[(char)c-'a'].add(new BigInteger("1"));
                    } catch (Exception e) {

                        e.printStackTrace();
                       // System.out.println((char)c+ " --------------------- ");




                    }

                }
            }
            return tempLetterCount;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BigInteger[26];



    }



    @Override
    protected BigInteger[] compute() {
        final List<FileCount> walks = new ArrayList<>();
        final List <BigInteger[]> countList = new ArrayList<>();
        try{

            Files.walkFileTree(filePath, new SimpleFileVisitor<>() {


                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (!dir.equals(FileCount.this.filePath)) {
                        FileCount w = new FileCount(dir);
                        w.fork();
                        walks.add(w);
                        //Arrays.stream(dir.toFile().listFiles(f -> f.isFile()));

                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toFile().isFile()) {
                        countList.add(countLowerCase(file.toFile()));
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }


            });
        }catch (Exception e){
            e.printStackTrace();
        }
        for (FileCount w : walks) {
            countList.add(w.join());
        }


        BigInteger [][] countArray = new BigInteger[countList.size()][26];
        countList.toArray(countArray);
        return incrementLetterAtIndex(countArray);




    }


    private   BigInteger[] incrementLetterAtIndex(BigInteger [] ... tempLetterCounts){
        BigInteger [] letterCount = new BigInteger[26];
        init(letterCount);
        for(BigInteger []temp:tempLetterCounts){
            for(int i=0;i<26;i++){
                letterCount[i]=letterCount[i].add(temp[i]);
            }
        }

        return letterCount;

    }

    private void init(BigInteger[] bigIntegers) {
        for (int i =0;i<bigIntegers.length;i++){
            bigIntegers[i] = new BigInteger("0");
        }

    }


}
