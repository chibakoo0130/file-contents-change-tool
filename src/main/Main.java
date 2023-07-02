package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Main {
	
	private static final String FILE_SEPARATOR = File.separator;
	private static final String CURRENT_DIR = System.getProperty("user.dir");
	private static final String BREAK_LINE = System.getProperty("line.separator");
	private static final String RESOURCES_DIR = CURRENT_DIR + FILE_SEPARATOR + "resources" + FILE_SEPARATOR;
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		
		LOGGER.info("tool開始");
		LOGGER.info(String.format("引数：%s 引数の数：%s", Arrays.toString(args), args.length));
		
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException("引数の数が不正です。引数は変更前の単語と変更後の単語のペアになるよう入力してください。");
		}
		
		final String pathsFilePath = RESOURCES_DIR + "paths.txt";

		try {
			
			checkBeforeReadingFile(pathsFilePath);
			
		} catch (NoSuchFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		List<String> targetReadFilePaths = new ArrayList<>();
		try (final BufferedReader br = Files.newBufferedReader(Paths.get(pathsFilePath), StandardCharsets.UTF_8)) {
			for(String line; (line = br.readLine()) != null;) {
				targetReadFilePaths.add(line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LOGGER.info("書き換え処理開始");
		
		for(final String targetPath : targetReadFilePaths) {
			
			LOGGER.info(String.format("書き換え対象ファイル：%s", targetPath));
			
			try {
				
				checkBeforeReadingFile(targetPath);
				
			} catch (NoSuchFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			final String contentsChangedFilePath = CURRENT_DIR + FILE_SEPARATOR + "output" + FILE_SEPARATOR + new File(targetPath).getName();

			if (!new File(contentsChangedFilePath).exists()) {
				try {
					Files.createFile(Paths.get(contentsChangedFilePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try (final BufferedReader br = Files.newBufferedReader(Paths.get(targetPath), StandardCharsets.UTF_8);
					final BufferedWriter bw = Files.newBufferedWriter(Paths.get(contentsChangedFilePath), 
							StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
				
				for(String line; (line = br.readLine()) != null;) {
					String changedLine = line;
					for (int i = 0; i < args.length; i+=2) {
						changedLine = changedLine.replaceAll(args[i], args[i+1]);
					}
					bw.append(changedLine);
					bw.append(BREAK_LINE);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		LOGGER.info("tool終了");
	}
	
	protected static void checkBeforeReadingFile(String filePath) throws NoSuchFileException, IOException {
		
		final File file = new File(filePath);
		
		if (!file.exists()) {
			throw new NoSuchFileException(String.format("ファイルが存在しません。以下のファイルパスを確認してください。"
					+ BREAK_LINE + "ファイルパス：%s", file));
		}
		
		if (!file.canRead()) {
			throw new IOException("ファイルが読み込み可能ではありません。権限を確認してください。");
		}
		
		if (Files.size(Path.of(filePath)) < 0) {
			throw new IOException("ファイルが空です。ファイルの中身を確認してください。");
		}
	}

}
