/**
 *
 */
package org.simon.pascal.controller;

import org.simon.pascal.helper.PrismicContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.prismic.Document;
import io.prismic.Response;

/**
 * @author nsimonin1
 *
 */
@Controller
public class ContentController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class DocumentNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    @Autowired
    private PrismicContext prismic;

    @GetMapping(value = {"/", "/documents"})
    public String index(@RequestParam(value = "ref", required = false) String ref, ModelMap model) {
        model.addAttribute("documents", prismic.getApi().getForm("everything").ref(prismic.getRef()).submit());
        model.addAttribute("prismic", prismic);
        return "docs";
    }

    @GetMapping("/documents/{id:[-_a-zA-Z0-9]{16}}/{slug}")
    public String detail(@PathVariable("id") String id, @PathVariable("slug") String slug, @RequestParam(value = "ref", required = false) String ref, ModelMap model) {
        final Document maybeDocument = prismic.getDocument(id);
        final String checked = prismic.checkSlug(maybeDocument, slug);
        if (checked == null) {
            model.addAttribute("document", maybeDocument);
            model.addAttribute("prismic", prismic);
            return "detail";
        } else if (checked == PrismicContext.DOCUMENT_NOT_FOUND) {
            throw new DocumentNotFoundException();
        } else {
            return "redirect:/documents/" + id + "/" + checked + "/" + (prismic.maybeRef() == null ? "" : "?ref=" + prismic.maybeRef());
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String q, @RequestParam(value = "ref", required = false) String ref, ModelMap model) {
        Response results = null;
        if (q != null && !q.trim().isEmpty()) {
            results = prismic.getApi().getForm("everything").query("[[:d = fulltext(document, \"" + q + "\")]]").ref(prismic.getRef()).submit();
        }
        model.addAttribute("q", q);
        model.addAttribute("results", results);
        model.addAttribute("prismic", prismic);
        return "search";
    }

}
