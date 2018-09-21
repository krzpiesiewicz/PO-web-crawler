/**
 * 
 */
package pl.edu.mimuw.crawler.kp385996;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Zarządca kolejki przetwarzania dokumentów.
 * 
 * @author Krzysztof Piesiewicz
 */
public class DocumentQueueManager {

	private LinkedList<DocumentLabel> queue;
	private HashSet<DocumentLabel> addedDocs;
	private Crawler crawler;

	/**
	 * @param crawler
	 */
	public DocumentQueueManager(Crawler crawler) {
		this.crawler = crawler;
		queue = new LinkedList<>();
		addedDocs = new HashSet<>();
	}

	/**
	 * Realizuje obsługę crawlera.
	 */
	public void startProccessing() {
		crawler.init(this);

		while (!queue.isEmpty()) {
			DocumentLabel docLabel = queue.remove();

			switch (docLabel.loadSource()) {
			case 0:	crawler.processPage(this, docLabel); break;
			case -1: crawler.processDocument(this, docLabel); break;
			}
			docLabel.releaseSource();
		}

		crawler.terminate(this);
	}

	/**
	 * Dodaje do kolejki dokument o podanym adresie www o ile nie został on już
	 * wcześniej dodany.
	 * 
	 * @param address
	 */
	public void addAddressToQueue(String address, Object information) {

		address = LinkUtil.getSimpleRepairedURL(address);
		DocumentLabel docLabel = new DocumentLabel(address, information);

		if (!addedDocs.contains(docLabel)) {
			queue.add(docLabel);
			addedDocs.add(docLabel);
		}
	}

	/**
	 * Dodaje do kolejki dokument o podanej ścieżce dyskowej (względnej lub
	 * bezwzględnej) o ile nie został on już wcześniej dodany.
	 * 
	 * @param parent
	 * @param relativePath
	 * @param information
	 */
	public void addFileToQueue(String parent, String relativePath, String baseURI,
			Object information) {

		DocumentLabel docLabel = new DocumentLabel(parent, relativePath, baseURI, information);

		if (!addedDocs.contains(docLabel)) {
			queue.add(docLabel);
			addedDocs.add(docLabel);
		}
	}

	/**
	 * Dodaje do kolejki podany znacznik dokumentu.
	 * 
	 * @param docLabel
	 */
	public void addToQueue(DocumentLabel docLabel) {
		queue.add(docLabel);
	}

	/**
	 * Przerywa proces przetwarzania stron z kolejki. Usuwa z kolejki referencje
	 * do dokumentów.
	 */
	public void stopProccessing() {
		queue.clear();
	}
}