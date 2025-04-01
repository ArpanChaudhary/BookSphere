// Main JavaScript for BookSphere

// Document Ready Function
document.addEventListener('DOMContentLoaded', function() {
    console.log('BookSphere app initialized');
    
    // Initialize any tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Initialize any popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                window.scrollTo({
                    top: targetElement.offsetTop - 70, // Offset for fixed header
                    behavior: 'smooth'
                });
            }
        });
    });
    
    // Book hover effect
    const bookCards = document.querySelectorAll('.book-card');
    if (bookCards.length) {
        bookCards.forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.querySelector('.book-img').style.transform = 'scale(1.05)';
            });
            
            card.addEventListener('mouseleave', function() {
                this.querySelector('.book-img').style.transform = 'scale(1)';
            });
        });
    }
    
    // Counter animation for stats section
    const statNumbers = document.querySelectorAll('.stat-number');
    if (statNumbers.length) {
        const observerOptions = {
            root: null,
            rootMargin: '0px',
            threshold: 0.1
        };
        
        const statsObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const target = entry.target;
                    const countTo = parseInt(target.getAttribute('data-count'), 10);
                    
                    if (!target.classList.contains('counted')) {
                        let count = 0;
                        const updateCount = () => {
                            const increment = countTo / 50;
                            if (count < countTo) {
                                count += increment;
                                target.textContent = Math.ceil(count).toLocaleString();
                                setTimeout(updateCount, 20);
                            } else {
                                target.textContent = countTo.toLocaleString();
                            }
                        };
                        
                        updateCount();
                        target.classList.add('counted');
                    }
                    
                    observer.unobserve(target);
                }
            });
        }, observerOptions);
        
        statNumbers.forEach(stat => {
            statsObserver.observe(stat);
        });
    }
    
    // Mobile menu toggle
    const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');
    if (mobileMenuToggle) {
        mobileMenuToggle.addEventListener('click', function() {
            document.querySelector('.navbar-collapse').classList.toggle('show');
        });
    }
});