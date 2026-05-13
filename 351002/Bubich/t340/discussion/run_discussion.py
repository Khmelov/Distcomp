from app import create_app

if __name__ == '__main__':
    app = create_app()
    print("Discussion service starting on http://localhost:24130")
    app.run(host='localhost', port=24130, debug=True)