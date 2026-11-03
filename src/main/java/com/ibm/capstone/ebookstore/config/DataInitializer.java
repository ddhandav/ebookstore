package com.ibm.capstone.ebookstore.config;

import com.ibm.capstone.ebookstore.entity.*;
import com.ibm.capstone.ebookstore.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            AddressRepository addressRepository,
            CategoryRepository categoryRepository,
            PublisherRepository publisherRepository,
            BookRepository bookRepository,
            CartRepository cartRepository) {

        return args -> {
            if (categoryRepository.count() > 0) {
                return; // Already initialized
            }

            // 1. Categories
            Category tech = categoryRepository.save(new Category("Technology & Computing", "Software development, AI, Cloud, and architecture books"));
            Category fiction = categoryRepository.save(new Category("Fiction & Literature", "Novels, science fiction, and bestselling stories"));
            Category business = categoryRepository.save(new Category("Business & Economics", "Leadership, startups, finance, and productivity"));
            Category science = categoryRepository.save(new Category("Science & Nature", "Physics, biology, space, and popular science"));

            // 2. Publishers / Brands
            Publisher oreilly = publisherRepository.save(new Publisher("O'Reilly Media", "United States"));
            Publisher penguin = publisherRepository.save(new Publisher("Penguin Random House", "United Kingdom"));
            Publisher harper = publisherRepository.save(new Publisher("HarperCollins", "United States"));
            Publisher packt = publisherRepository.save(new Publisher("Packt Publishing", "United Kingdom"));
            Publisher pearson = publisherRepository.save(new Publisher("Pearson Education", "United Kingdom"));

            // 3. Books
            bookRepository.saveAll(List.of(
                    new Book("Designing Data-Intensive Applications", "Martin Kleppmann", "9781449373320",
                            "The big ideas behind reliable, scalable, and maintainable systems.",
                            new BigDecimal("49.99"), tech, oreilly, 50, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c", 2, 4.9),

                    new Book("Building Microservices", "Sam Newman", "9781492034025",
                            "Designing Fine-Grained Systems with modern microservices patterns and cloud deployments.",
                            new BigDecimal("44.50"), tech, oreilly, 40, "https://images.unsplash.com/photo-1512820790803-83ca734da794", 2, 4.8),

                    new Book("Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", "9780132350884",
                            "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.",
                            new BigDecimal("39.99"), tech, pearson, 60, "https://images.unsplash.com/photo-1532012164546-f432f2e37229", 1, 4.7),

                    new Book("Cloud Native Spring in Action", "Thomas Vitale", "9781617298813",
                            "Practices and patterns for building robust cloud-native applications with Spring Boot and Kubernetes.",
                            new BigDecimal("42.00"), tech, packt, 35, "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f", 2, 4.8),

                    new Book("Generative AI on AWS", "Chris Fregly, Antje Barth", "9781098159221",
                            "Building, training, and deploying Foundation Models and LLMs in production.",
                            new BigDecimal("54.99"), tech, oreilly, 25, "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6", 2, 4.9),

                    new Book("The Midnight Library", "Matt Haig", "9780525559474",
                            "Between life and death there is a library, and within that library, the shelves go on forever.",
                            new BigDecimal("18.99"), fiction, penguin, 100, "https://images.unsplash.com/photo-1497633762265-9d179a990aa6", 3, 4.6),

                    new Book("Project Hail Mary", "Andy Weir", "9780593135204",
                            "A lone astronaut must save the earth from disaster in this thrilling science fiction masterpiece.",
                            new BigDecimal("21.50"), fiction, penguin, 80, "https://images.unsplash.com/photo-1516979187457-637abb4f9353", 2, 4.9),

                    new Book("Dune", "Frank Herbert", "9780441172719",
                            "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides.",
                            new BigDecimal("16.99"), fiction, harper, 75, "https://images.unsplash.com/photo-1476275466078-4007374efbbe", 1, 4.8),

                    new Book("Atomic Habits", "James Clear", "9780735211292",
                            "An Easy & Proven Way to Build Good Habits & Break Bad Ones.",
                            new BigDecimal("22.00"), business, penguin, 120, "https://images.unsplash.com/photo-1544947950-fa07a98d237f", 1, 4.9),

                    new Book("Zero to One: Notes on Startups", "Peter Thiel, Blake Masters", "9780804139298",
                            "How to build companies that create new things and go from 0 to 1.",
                            new BigDecimal("19.99"), business, harper, 50, "https://images.unsplash.com/photo-1589829085413-56de8ae18c73", 2, 4.5),

                    new Book("A Brief History of Time", "Stephen Hawking", "9780553380163",
                            "From the Big Bang to Black Holes - exploring the profound questions of our universe.",
                            new BigDecimal("17.50"), science, penguin, 45, "https://images.unsplash.com/photo-1451187580459-43490279c0fa", 3, 4.8)
            ));

            // 4. Sample Test User & Address
            User testUser = userRepository.save(new User("John Doe", "john.doe@example.com", "password123", 500));
            addressRepository.save(new Address("John Doe", "123 Innovation Blvd, Suite 400", "Austin", "TX", "78701", "USA", true, testUser));
            cartRepository.save(new Cart(testUser));
        };
    }
}
