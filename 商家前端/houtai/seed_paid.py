import pymysql
c = pymysql.connect(host='localhost', user='root', password='123456', db='termshop', charset='utf8mb4')
cur = c.cursor()
cur.execute("UPDATE orders SET status=2 WHERE order_no IN (%s, %s)", ('ORD2026062513333721CD0B88','ORD20260625132934F188C27A'))
print('updated:', cur.rowcount)
c.commit()
c.close()
