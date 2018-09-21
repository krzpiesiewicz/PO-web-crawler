/**
 * 
 */
package pl.edu.mimuw.crawler.kp385996;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Krzysztof Piesiewicz
 */
public class Demo1 {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("Demo1 oczekuje dokładnie 1 argumentu.");
			return;
		} else {
			Crawler1 crawler = new Crawler1(args[0]);
			DocumentQueueManager docManager = new DocumentQueueManager(crawler);
			docManager.startProccessing();
		}
	}

	private static class Crawler1 implements Crawler {

		private String address, sourceDomain;
		private HashMap<String, Integer> domains;

		public Crawler1(String address) {

			this.address = address;
			domains = new HashMap<>();
		}

		@Override
		public void init(DocumentQueueManager docManager) {

			address = LinkUtil.getDecodedAddressOrPath(LinkUtil.getSimpleRepairedURL(address));
			sourceDomain = LinkUtil.getDomainWithoutProtocolPrefix(address);

			docManager.addAddressToQueue(address, null);
		}

		@Override
		public void terminate(DocumentQueueManager docManager) {

			ArrayList<String> keys = new ArrayList<String>(domains.keySet());
			keys.sort(new DomainComparator());

			for (String key : keys) {
				System.out.println(key + " " + domains.get(key));
			}
		}

		@Override
		public void processPage(DocumentQueueManager docManager, DocumentLabel docLabel) {

			ArrayList<String> absLinks = docLabel.getAbsoluteLinks();

			for (int i = 0; i < absLinks.size(); i++) {
				absLinks.set(i, LinkUtil
						.getDecodedAddressOrPath(LinkUtil.getSimpleRepairedURL(absLinks.get(i))));
				String domain = LinkUtil.getDomainWithoutProtocolPrefix(absLinks.get(i));

				if (domain.equals(sourceDomain)) {
					if (LinkUtil.mightBeAHtmlFilePathOrAddress(absLinks.get(i))) {
						docManager.addAddressToQueue(absLinks.get(i), null);
					}
				} else if (!domain.startsWith("mailto:")) {
					domains.put(domain, domains.getOrDefault(domain, 0) + 1);
				}
			}
		}

		private class DomainComparator implements Comparator<String> {
			@Override
			public int compare(String s1, String s2) {
				Integer n1 = domains.get(s1), n2 = domains.get(s2);
				if (n1.equals(n2)) { // wprzypadku równej liczby wystąpień
					return s1.compareTo(s2); // sortujemy alfabetycznie
				}
				return n2.compareTo(n1); // sortujemy nierosnąco po liczbie
											// wystąpień
			}
		}

		@Override
		public void processDocument(DocumentQueueManager docManager, DocumentLabel docLabel) {
			System.err.println("Loading the source of the document from address \""
					+ docLabel.getAddress() + "\" failed.");
		}
	}
}
