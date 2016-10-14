import gym
env = gym.make('CartPole-v0')

# 1エピソードでの行動数
T = 200

#環境の初期化
def set_initial_state():
    env.reset()
    obstr = ""
    for num in env.state:
        obstr += str(num) + " "
    print(obstr)

#1行動実行
def do_action():
    # 環境の取得
    observation = list(map(float, input().split()))
    env.state = observation

    # 行動の取得
    action = int(input())

    # 画面表示
    # env.render()

    # 行動実行
    observation, reward, done, info = env.step(action)

    # 環境, 報酬, 終了判定を返す
    obstr = ""
    for num in observation:
        obstr += str(num) + " "

    print(obstr)
    print(reward)
    print(done)

#1エピソード実行
def do_one_episode():
    r_total = 0.0
    env.reset()
    for t in range(T):
        # env.render()
        action = env.action_space.sample()
        observation, reward, done, info = env.step(action)
        r_total += reward

        if done:
            # print("Episode finished after {} timesteps".format(t+1))
            break

    print(r_total)

#ループの終了
def quit():
    return True

#無限ループ
while True:
    operation = input()
    if operation == "init_state":
        set_initial_state()

    elif operation == "do_action":
        do_action()

    elif operation == "do_one_episode":
        do_one_episode()

    elif operation == "quit":
        quit()
        break
