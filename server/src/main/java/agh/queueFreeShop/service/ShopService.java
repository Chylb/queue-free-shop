package agh.queueFreeShop.service;

import agh.queueFreeShop.model.CartItem;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.repository.ProductRepository;
import agh.queueFreeShop.repository.ReceiptRepository;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ShopService {
    private final ProductRepository productRepository;
    private final ShoppingCartRepository cartRepository;
    private final ReceiptRepository receiptRepository;

    ShopService(ProductRepository productRepository, ShoppingCartRepository cartRepository, ReceiptRepository receiptRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.receiptRepository = receiptRepository;
    }

    @Transactional
    public void addProductToCart(ShoppingCart cart, String barcode){
        Product product =  productRepository.findByBarcode(barcode);

        if(product == null)
            throw new IllegalArgumentException("Can't find product");

        CartItem cartItem = cart.getCartItem(product);
        if(cartItem == null){
            cartItem = new CartItem(cart, product, 1);
            cart.getItems().add(cartItem);
        }else {
            cartItem.addOne();
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void removeProductFromCart(ShoppingCart cart, String barcode){
        Product product =  productRepository.findByBarcode(barcode);

        if(product == null)
            throw new IllegalArgumentException("Can't find product");

        CartItem cartItem = cart.getCartItem(product);
        if(cartItem != null){
            cartItem.removeOne();
        }

        cartRepository.save(cart);
    }

    @Transactional
    public Receipt finalizeShopping(ShoppingCart cart) {
        Receipt receipt = cart.generateReceipt();
        cartRepository.delete(cart);
        return receiptRepository.save(receipt);
    }
}
