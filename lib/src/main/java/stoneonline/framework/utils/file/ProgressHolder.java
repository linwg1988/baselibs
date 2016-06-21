package stoneonline.framework.utils.file;

public class ProgressHolder {
	public long currentFileProgress;
	public long currentFileLength;

	public long allFilesProgress;
	public long allFilesLength;

	public int fileIndex = 0;

	public boolean hasDone = false;

	public boolean isOver() {
		return allFilesLength == allFilesProgress;
	}
}