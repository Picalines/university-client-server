pragma solidity ^0.8.0;

contract SimpleStore {
    address public owner;
    mapping(address => uint256) public balances;
    mapping(uint256 => Product) public products;

    uint256 public productCount = 0;

    struct Product {
        uint256 id;
        string name;
        uint256 price;
        uint256 quantity;
    }

    event ProductAdded(Product product);

    event ProductPurchased(Product product);

    modifier onlyOwner() {
        require(msg.sender == owner, "Only the owner can call this function");
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    function addProduct(
        string memory _name,
        uint256 _price,
        uint256 _quantity
    ) public onlyOwner {
        uint256 id = ++productCount;
        emit ProductAdded(
            products[productCount] = Product(id, _name, _price, _quantity)
        );
    }

    function purchaseProduct(uint256 _productId, uint256 _quantity)
        public
        payable
    {
        require(
            _productId > 0 && _productId <= productCount,
            "Invalid product ID"
        );

        Product storage product = products[_productId];
        require(_productId != 0, "Product not found");

        require(product.quantity >= _quantity, "Not enough stock available");

        uint256 totalCost = product.price * _quantity;
        require(msg.value >= totalCost, "Insufficient funds sent");

        balances[owner] += totalCost;
        balances[msg.sender] -= totalCost;
        product.quantity -= _quantity;

        emit ProductPurchased(
            Product(_productId, product.name, product.price, _quantity)
        );

        if (msg.value > totalCost) {
            payable(msg.sender).transfer(msg.value - totalCost);
        }
    }

    function withdrawBalance() public onlyOwner {
        uint256 balance = balances[owner];
        require(balance > 0, "No balance to withdraw");
        balances[owner] = 0;
        payable(owner).transfer(balance);
    }
}
