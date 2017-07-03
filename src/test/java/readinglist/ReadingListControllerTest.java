package readinglist;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@WebMvcTest(ReadingListController.class)
@WebAppConfiguration
public class ReadingListControllerTest {
    
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private ReadingListRepository readingListRepository;
    
    @Test
    public void testReadingListView() throws Exception {
        mvc.perform(get("/readingList/long").accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string(Matchers.containsString("Your Reading List")));
    }
    
    @Test
    public void testAddBookToReadingList() throws Exception {
        mvc.perform(post("/readingList/long")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Spring in Action")
                .param("author", "Craig Wills")
                .param("isbn", "123-456-7890")
                .param("description", "An interesting book about Spring Boot"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/readingList/long"));
        
        Book expectedBook = new Book();
        expectedBook.setId(1L);
        expectedBook.setReader("long");
        expectedBook.setTitle("Spring in Action");
        expectedBook.setAuthor("Craig Wills");
        expectedBook.setIsbn("123-456-7890");
        expectedBook.setDescription("An interesting book about Spring Boot");
        
        mvc.perform(get("/readingList/long"))
            .andExpect(status().isOk())
            .andExpect(view().name("readingList"))
            .andExpect(model().attributeExists("books"))
            .andExpect(model().attribute("books", hasSize(1)))
            .andExpect(model().attribute("books", contains(samePropertyValuesAs(expectedBook))))
            ;
    }
    
    @Test
    public void testHomeReadingListPageWithNoBook() throws Exception {
        mvc.perform(get("/readingList/long"))
            .andExpect(status().isOk())
            .andExpect(view().name("readingList"))
            .andExpect(model().attributeExists("books"))
            .andExpect(model().attribute("books", is(empty())));
    }

}
