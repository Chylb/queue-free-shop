import pandas as pd 
import math
import random
import re

#converts https://world.openfoodfacts.org/ csv file into sql statement

df = pd.read_csv("openfoodfacts_search.csv", encoding='utf8', sep='\t', low_memory=False) 

fout = open("products.sql", "w", encoding='utf8')

columns = [col for col in df.columns]

def get_weight(quantity):
    if not isinstance(quantity, str): return 0

    split = quantity.split(' ')    
    if len(split) < 2: return 0
    
    unit = split[1]
    weight = split[0]
    try:
        weight = float(weight)
    except ValueError:
        return 0
    
    coef = 0
    if unit == 'g' or unit == 'ml':
        coef = 1
    elif unit == 'kg' or unit == 'l':
        coef = 1000
        
    weight = weight * coef   
    return int(weight)

def get_price(weight):
    price = math.sqrt((weight+20)/1000.0) * 500
    price = price * random.uniform(0.5, 2)
    return int(price)
    

for index, row in df.iterrows():
    quantity = row["quantity"]    
    weight = get_weight(quantity)
    if weight == 0: continue

    price = get_price(weight)
    barcode = row['code']
    
    if len(barcode) != 13: continue

    image = row['image_small_url']
    if not isinstance(image, str): continue
        
    product_name = row['product_name']
    if not isinstance(product_name, str): continue

    product_name = product_name +" "+quantity
    product_name = re.sub("'", "''", product_name)
    
    out_row = "('"+str(product_name)+"',"+str(price)+",'"+barcode+"',"+str(weight)+",'"+image+"'),\n"
    
    fout.write(out_row)

#print(df.head())
#print(columns)
