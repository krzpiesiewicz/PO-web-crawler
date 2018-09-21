/**
 * 
 */
package pl.edu.mimuw.crawler.kp385996;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Krzysztof Piesiewicz
 */
public class Demo2 {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Demo2 oczekuje dokładnie 2 argumentów.");
			return;
		} else {
			Crawler2 crawler = new Crawler2(args[0], new Integer(args[1]));
			DocumentQueueManager docManager = new DocumentQueueManager(crawler);
			docManager.startProccessing();
		}
	}

	private static class Crawler2 implements Crawler {

		private String diskPath;
		private int numberOfSubpages, maxLevel;

		public Crawler2(String diskPath, int maxLength) {
			this.diskPath = diskPath;
			this.maxLevel = maxLength;
		}

		@Override
		public void init(DocumentQueueManager docManager) {
			String parent = new File(diskPath).getParent() + File.separator;
			docManager.addFileToQueue(parent, diskPath.substring(parent.length()), parent,
					new Integer(0));
		}

		@Override
		public void terminate(DocumentQueueManager docManager) {
			System.out.println(numberOfSubpages);
		}

		@Override
		public void processPage(DocumentQueueManager docManager, DocumentLabel docLabel) {

			numberOfSubpages++;

			if ((int) docLabel.getInformation() < maxLevel) {
				ArrayList<String> links = docLabel.getLinks();

				for (int i = 0; i < links.size(); i++) {
					links.set(i, LinkUtil.getURLWithoutQueries(links.get(i)));

					if (!LinkUtil.isInternetAddress(links.get(i))
							&& LinkUtil.mightBeAHtmlFilePathOrAddress(links.get(i))) {
						docManager.addFileToQueue(docLabel.getParent(), links.get(i),
								docLabel.getBaseURI(), (int) docLabel.getInformation() + 1);
					}
				}
			}
		}

		@Override
		public void processDocument(DocumentQueueManager docManager, DocumentLabel docLabel) {
			System.err.println("Loading the source of the document from file with diskPath \""
					+ docLabel.getPath() + "\" failed.");
		}
	}
}
