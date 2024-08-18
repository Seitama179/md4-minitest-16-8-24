package com.example.bookminitest.controller;


import com.example.bookminitest.model.Book;
import com.example.bookminitest.model.BookForm;
import com.example.bookminitest.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private IBookService bookService;

    @GetMapping("/")
    public String index(Model model) {
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        return "/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("book", new Book());
        return "/create";
    }

    @Value("${file-upload}")
    private String upload;

    @PostMapping("/create")
    public String save(@ModelAttribute BookForm bookForm) {
        MultipartFile file = bookForm.getImage();
        String fileName = file.getOriginalFilename();
        try {
            FileCopyUtils.copy(bookForm.getImage().getBytes(), new File(upload + fileName));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Book book = new Book(bookForm.getId(), bookForm.getName(), bookForm.getAuthor(),bookForm.getPrice(), bookForm.getCategory(), fileName);
        bookService.save(book);
        return "redirect:/books/";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable int id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "/detail";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BookForm bookForm) {
        MultipartFile file = bookForm.getImage();
        String fileName = file.getOriginalFilename();
        try {
            FileCopyUtils.copy(file.getBytes(), new File(upload+fileName));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Book book = new Book();
        book.setName(bookForm.getName());
        book.setAuthor(bookForm.getAuthor());
        book.setPrice(bookForm.getPrice());
        book.setCategory(bookForm.getCategory());
        book.setImage(fileName);
        book.setId(bookForm.getId());
        bookService.update(book.getId(), book);
        return "redirect:/books/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model) {
        Book book = bookService.findById(id);
        bookService.remove(book.getId());
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        model.addAttribute("message", "Xóa thành công!");
        return "/index";
    }
}
