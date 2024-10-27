package io.xeros.model.collisionmap;

public class MemoryArchive {

	private final ByteStream cache;
	private final ByteStream index;
	private static final int INDEX_DATA_CHUNK_SIZE = 12;

	public MemoryArchive(ByteStream cache, ByteStream index) {
		this.cache = cache;
		this.index = index;
	}

	public byte[] get(int dataIndex) {
		try {
			if (index.length() < (dataIndex * INDEX_DATA_CHUNK_SIZE))
				return null;
			index.setOffset(dataIndex * INDEX_DATA_CHUNK_SIZE);
			long fileOffset = index.getLong();
			int fileSize = index.getInt();
			cache.setOffset((int) fileOffset);
            return cache.read(fileSize);
		} catch (Exception e) {
			System.out.println("MemoryArchive - Check for error");
			e.printStackTrace(System.err);
			return null;
		}
	}

	public int contentSize() {
		return index.length() / 12;
	}

}
