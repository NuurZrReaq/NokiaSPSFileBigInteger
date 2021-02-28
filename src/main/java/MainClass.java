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
/*
        if (args.length !=1) {
            //throw new RuntimeException();
        } else*/
            String path = "C:\\Users\\NoorZ\\Documents\\dir";

            ForkJoinPool pool = new ForkJoinPool(8);
            FileCount fileCount = new FileCount(Paths.get(path).toRealPath());
            //FileCount fileCount = new FileCount(Paths.get("C:\\Users\\NoorZ\\Documents\\dir").toRealPath());
            //FileCount fileCount = new FileCount(Paths.get("C:\\Users\\NoorZ\\Documents\\NokiaTaskFileConcurrency\\directory").toRealPath());
            Long current = System.currentTimeMillis();
            BigInteger [] bigIntegers = pool.invoke(fileCount);
            Long after = System.currentTimeMillis();
            for(int i=0;i<bigIntegers.length;i++){
                System.out.print((char)(i+'a') + "\t"+bigIntegers[i]+'\n');
            }
            //fileCount.printLetterCount();
            System.out.println();
            System.out.println(after - current);

    }




}




class FileCount extends RecursiveTask<BigInteger[]> {



    private final Path filePath;


    public FileCount(Path filePath) {
        this.filePath = filePath;
    }




    public BigInteger[] countLowerCase(File file) {
        int[] tempLetterCount = new int[26];
        //init(tempLetterCount);
        char [] charArray = new char[16*1024];
        int numChar;
        try( BufferedReader fileReader = new BufferedReader(new FileReader(file))) {

            while ((numChar = fileReader.read(charArray)) >0) {

                for(int i =0;i<numChar;i++){
                    if (charArray[i]>='a'&& charArray[i] <='z') {

                        try {
                            tempLetterCount[charArray[i]-'a']++;
                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }

                }

            }


            return change(tempLetterCount);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new BigInteger[26];


    }


    private BigInteger[] change(int[] bigIntegers) {
        BigInteger [] bigIntegers1 = new BigInteger[26];
        for (int i = 0; i < bigIntegers.length; i++) {
            bigIntegers1[i] = new BigInteger(Integer.toString(bigIntegers[i]));
        }
        return bigIntegers1;

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
