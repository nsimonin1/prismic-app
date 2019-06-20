/**
 *
 */
package org.simon.pascal.helper;

import org.springframework.stereotype.Component;

import io.prismic.Fragment;
import io.prismic.LinkResolver;
/**
 * @author nsimonin1
 *
 */
@Component
public class MyLinkResolver implements LinkResolver {

    private PrismicContext prismic;

    public void setPrismicContext(PrismicContext prismic) {
        this.prismic = prismic;
    }

    @Override
	public String resolve(Fragment.DocumentLink link) {
        final StringBuilder sb = new StringBuilder("/documents/").append(link.getId()).append("/").append(link.getSlug());
        if (prismic.maybeRef() != null) {
			sb.append("?ref=").append(prismic.maybeRef());
		}
        return sb.toString();
    }

}
