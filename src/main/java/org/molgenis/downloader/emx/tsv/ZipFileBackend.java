package org.molgenis.downloader.emx.tsv;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.downloader.api.EMXBackend;
import org.molgenis.downloader.api.EMXDataStore;

import static org.molgenis.downloader.util.ConsoleWriter.writeToConsole;

public class ZipFileBackend implements EMXBackend
{

	private FileSystem fs;
	private final List<TSVFile> files;

	public ZipFileBackend(final Path path) throws IOException, URISyntaxException
	{
		files = new ArrayList<>();
		final Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", StandardCharsets.UTF_8.toString());
		final URI uri = new URI("jar:file:" + path.toAbsolutePath());
		fs = FileSystems.newFileSystem(uri, env);
	}

	@Override
	public EMXDataStore createDataStore(final String name) throws IOException
	{
		final TSVFile sheet = new TSVFile(fs, name);
		files.add(sheet);
		return sheet;
	}

	@Override
	public void close() throws Exception
	{
		for (TSVFile file : files)
		{
			file.close();
		}
		fs.close();
	}
}
